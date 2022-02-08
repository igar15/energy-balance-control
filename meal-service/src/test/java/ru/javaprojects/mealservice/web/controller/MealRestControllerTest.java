package ru.javaprojects.mealservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.mealservice.MealMatcher;
import ru.javaprojects.mealservice.TestUtil;
import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.repository.MealRepository;
import ru.javaprojects.mealservice.service.MealService;
import ru.javaprojects.mealservice.to.MealTo;
import ru.javaprojects.mealservice.util.exception.ErrorType;
import ru.javaprojects.mealservice.web.json.JsonUtil;
import ru.javaprojects.mealservice.web.security.JwtProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javaprojects.mealservice.testdata.MealTestData.*;
import static ru.javaprojects.mealservice.util.exception.ErrorType.UNAUTHORIZED_ERROR;
import static ru.javaprojects.mealservice.util.exception.ErrorType.VALIDATION_ERROR;

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
                .param("page", "0")
                .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        List<Meal> meals = JsonUtil.readContentFromPage(actions.andReturn().getResponse().getContentAsString(), Meal.class);
        MealMatcher.assertMatch(meals, meal7, meal6, meal5, meal4, meal3);
    }

    @Test
    void getPageUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("page", "0")
                .param("size", "5"))
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
        MealMatcher.assertMatch(repository.findById(newId).get(), newMeal);
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
        newMealTo.setDescription("s");
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMealTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void getTotalCalories() {
    }
}