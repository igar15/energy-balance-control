package ru.javaprojects.mealservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.energybalancecontrolshared.test.TestUtil;
import ru.javaprojects.energybalancecontrolshared.test.WithMockCustomUser;
import ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.energybalancecontrolshared.web.json.JsonUtil;
import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.repository.MealRepository;
import ru.javaprojects.mealservice.to.MealTo;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.*;
import static ru.javaprojects.energybalancecontrolshared.web.security.SecurityConstants.NOT_AUTHORIZED;
import static ru.javaprojects.mealservice.testdata.MealTestData.*;
import static ru.javaprojects.mealservice.testdata.UserTestData.*;
import static ru.javaprojects.mealservice.web.AppExceptionHandler.EXCEPTION_DUPLICATE_DATE_TIME;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
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
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void getPage() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(PAGE_NUMBER_PARAM, PAGE_NUMBER)
                .param(PAGE_SIZE_PARAM, PAGE_SIZE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        List<Meal> meals = JsonUtil.readContentFromPage(actions.andReturn().getResponse().getContentAsString(), Meal.class);
        MEAL_MATCHER.assertMatch(meals, meal7, meal6, meal5, meal4, meal3);
    }

    @Test
    void getPageUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(PAGE_NUMBER_PARAM, PAGE_NUMBER)
                .param(PAGE_SIZE_PARAM, PAGE_SIZE))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void create() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewTo())))
                .andExpect(status().isCreated());
        Meal created = TestUtil.readFromJson(actions, Meal.class);
        long newId = created.id();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(repository.findByIdAndUserId(newId, USER1_ID).get(), newMeal);
    }

    @Test
    void createUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewTo())))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
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
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
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
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void update() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedTo())))
                .andExpect(status().isNoContent());
        MEAL_MATCHER.assertMatch(repository.findByIdAndUserId(MEAL1_ID, USER1_ID).get(), getUpdated());
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
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
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
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
    @WithMockCustomUser(userId = USER2_ID_STRING, userRoles = {USER_ROLE})
    void updateNotOwn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedTo())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void updateUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedTo())))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
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
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
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
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> repository.findByIdAndUserId(MEAL1_ID, USER1_ID).orElseThrow(() -> new NotFoundException("Not found meal with id=" + MEAL1_ID)));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void deleteNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithMockCustomUser(userId = USER2_ID_STRING, userRoles = {USER_ROLE})
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
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void getTotalCalories() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + TOTAL_CALORIES_ENDPOINT)
                .param(DATE_PARAM, DATE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String totalCalories = actions.andReturn().getResponse().getContentAsString();
        assertEquals(TOTAL_CALORIES, totalCalories);
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void getTotalCaloriesWhenNoMeals() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + TOTAL_CALORIES_ENDPOINT)
                .param(DATE_PARAM, LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String totalCalories = actions.andReturn().getResponse().getContentAsString();
        assertEquals(ZERO_CALORIES, totalCalories);
    }

    @Test
    void getTotalCaloriesUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + TOTAL_CALORIES_ENDPOINT)
                .param(DATE_PARAM, DATE))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void wrongRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "AAA/BBB/CCC"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(errorType(WRONG_REQUEST));
    }
}