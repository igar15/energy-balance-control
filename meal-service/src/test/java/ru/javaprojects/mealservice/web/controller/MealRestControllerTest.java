package ru.javaprojects.mealservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.mealservice.MealMatcher;
import ru.javaprojects.mealservice.TestUtil;
import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.repository.MealRepository;
import ru.javaprojects.mealservice.to.MealTo;
import ru.javaprojects.mealservice.util.exception.ErrorType;
import ru.javaprojects.mealservice.util.exception.NotFoundException;
import ru.javaprojects.mealservice.web.json.JsonUtil;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javaprojects.mealservice.testdata.MealTestData.*;
import static ru.javaprojects.mealservice.util.exception.ErrorType.*;
import static ru.javaprojects.mealservice.web.AppExceptionHandler.EXCEPTION_DUPLICATE_DATE_TIME;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class MealRestControllerTest {
    private static final String REST_URL = MealRestController.REST_URL + '/';

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MealRepository repository;

    public ResultMatcher errorType(ErrorType type) {
        return jsonPath("$.type").value(type.name());
    }

    public ResultMatcher detailMessage(String code) {
        return jsonPath("$.details").value(code);
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void getPage() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("page", PAGE_NUMBER)
                .param("size", PAGE_SIZE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        List<Meal> meals = JsonUtil.readContentFromPage(actions.andReturn().getResponse().getContentAsString(), Meal.class);
        MealMatcher.assertMatch(meals, meal7, meal6, meal5, meal4, meal3);
    }

    @Test
    void getPageUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("page", PAGE_NUMBER)
                .param("size", PAGE_SIZE))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void create() throws Exception {
        MealTo newMealTo = getNewTo();
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMealTo)))
                .andExpect(status().isCreated());
        Meal created = TestUtil.readFromJson(actions, Meal.class);
        long newId = created.id();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        MealMatcher.assertMatch(created, newMeal);
        MealMatcher.assertMatch(repository.findByIdAndUserId(newId, USER1_ID).get(), newMeal);
    }

    @Test
    void createUnAuth() throws Exception {
        MealTo newMealTo = getNewTo();
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMealTo)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void createInvalid() throws Exception {
        MealTo newMealTo = getNewTo();
        newMealTo.setDescription(INVALID_MEAL_DESCRIPTION);
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMealTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        MealTo newMealTo = getNewTo();
        newMealTo.setDateTime(meal1.getDateTime());
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMealTo)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_DATE_TIME));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void update() throws Exception {
        MealTo updatedTo = getUpdatedTo();
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isNoContent());
        MealMatcher.assertMatch(repository.findByIdAndUserId(MEAL1_ID, USER1_ID).get(), getUpdated());
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void updateIdNotConsistent() throws Exception {
        MealTo updatedTo = getUpdatedTo();
        updatedTo.setId(MEAL1_ID + 1);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void updateNotFound() throws Exception {
        MealTo updatedTo = getUpdatedTo();
        updatedTo.setId(NOT_FOUND);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithMockCustomUser(userId = USER2_ID_STRING)
    void updateNotOwn() throws Exception {
        MealTo updatedTo = getUpdatedTo();
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void updateUnAuth() throws Exception {
        MealTo updatedTo = getUpdatedTo();
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void updateInvalid() throws Exception {
        MealTo updatedTo = getUpdatedTo();
        updatedTo.setCalories(INVALID_MEAL_CALORIES);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        MealTo updatedTo = getUpdatedTo();
        updatedTo.setDateTime(meal2.getDateTime());
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_DATE_TIME));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> repository.findByIdAndUserId(MEAL1_ID, USER1_ID).orElseThrow(() -> new NotFoundException("Not found meal with id=" + MEAL1_ID)));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithMockCustomUser(userId = USER2_ID_STRING)
    void deleteNotOwn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void deleteUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void getTotalCalories() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "total-calories")
                .param("date", DATE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String totalCalories = actions.andReturn().getResponse().getContentAsString();
        assertEquals(TOTAL_CALORIES, totalCalories);
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void getTotalCaloriesWhenNoMeals() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "total-calories")
                .param("date", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String totalCalories = actions.andReturn().getResponse().getContentAsString();
        assertEquals(ZERO_CALORIES, totalCalories);
    }

    @Test
    void getTotalCaloriesUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "total-calories")
                .param("date", DATE))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }
}