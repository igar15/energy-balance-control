package ru.javaprojects.trainingservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.energybalancecontrolshared.test.TestUtil;
import ru.javaprojects.energybalancecontrolshared.test.WithMockCustomUser;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.energybalancecontrolshared.web.json.JsonUtil;
import ru.javaprojects.trainingservice.model.Exercise;
import ru.javaprojects.trainingservice.repository.ExerciseRepository;
import ru.javaprojects.trainingservice.to.ExerciseTo;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.*;
import static ru.javaprojects.energybalancecontrolshared.web.security.SecurityConstants.NOT_AUTHORIZED;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.*;
import static ru.javaprojects.trainingservice.testdata.UserTestData.*;
import static ru.javaprojects.trainingservice.web.AppExceptionHandler.EXCEPTION_DUPLICATE_DATE_TIME;

class ExerciseControllerTest extends AbstractControllerTest {
    private static final String REST_URL = ExerciseController.REST_URL + '/';

    @Autowired
    private ExerciseRepository repository;

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void getPage() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(REST_URL)
                .param(PAGE_NUMBER_PARAM, PAGE_NUMBER)
                .param(PAGE_SIZE_PARAM, PAGE_SIZE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        List<Exercise> exercises = JsonUtil.readContentFromPage(actions.andReturn().getResponse().getContentAsString(), Exercise.class);
        EXERCISE_MATCHER.assertMatch(exercises, exercise6, exercise5, exercise4);
    }

    @Test
    void getPageUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param(PAGE_NUMBER_PARAM, PAGE_NUMBER)
                .param(PAGE_SIZE_PARAM, PAGE_SIZE))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void create() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewTo())))
                .andExpect(status().isCreated());
        Exercise created = TestUtil.readFromJson(actions, Exercise.class);
        long newId = created.id();
        Exercise newExercise = getNew();
        newExercise.setId(newId);
        EXERCISE_MATCHER.assertMatch(created, newExercise);
        EXERCISE_MATCHER.assertMatch(repository.findByIdAndExerciseType_UserId(newId, USER1_ID).get(), newExercise);
    }

    @Test
    void createUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewTo())))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void createInvalid() throws Exception {
        ExerciseTo newTo = getNewTo();
        newTo.setDateTime(null);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        ExerciseTo newTo = getNewTo();
        newTo.setDateTime(exercise1.getDateTime());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_DATE_TIME));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedTo())))
                .andExpect(status().isNoContent());
        EXERCISE_MATCHER.assertMatch(repository.findByIdAndExerciseType_UserId(EXERCISE1_ID, USER1_ID).get(), getUpdated());
    }

    @Test
    void updateUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedTo())))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void updateIdNotConsistent() throws Exception {
        ExerciseTo updatedTo = getUpdatedTo();
        updatedTo.setId(EXERCISE1_ID + 1);
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void updateNotFound() throws Exception {
        ExerciseTo updatedTo = getUpdatedTo();
        updatedTo.setId(NOT_FOUND);
        perform(MockMvcRequestBuilders.put(REST_URL + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithMockCustomUser(userId = USER2_ID_STRING, userRoles = {USER_ROLE})
    void updateNotOwn() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedTo())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        ExerciseTo updatedTo = getUpdatedTo();
        updatedTo.setDateTime(exercise2.getDateTime());
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_DATE_TIME));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void updateInvalid() throws Exception {
        ExerciseTo updatedTo = getUpdatedTo();
        updatedTo.setAmount(INVALID_AMOUNT);
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + EXERCISE1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> repository.findByIdAndExerciseType_UserId(EXERCISE1_ID, USER1_ID)
                .orElseThrow(() -> new NotFoundException("Not found exercise with id=" + EXERCISE1_ID)));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithMockCustomUser(userId = USER2_ID_STRING, userRoles = {USER_ROLE})
    void deleteNotOwn() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + EXERCISE1_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void deleteUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + EXERCISE1_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void getTotalCaloriesBurned() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(REST_URL + TOTAL_CALORIES_BURNED_ENDPOINT)
                .param(DATE_PARAM, DATE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String totalCaloriesBurned = actions.andReturn().getResponse().getContentAsString();
        assertEquals(TOTAL_CALORIES_BURNED, totalCaloriesBurned);
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void getTotalCaloriesBurnedWhenNoExercises() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(REST_URL + TOTAL_CALORIES_BURNED_ENDPOINT)
                .param(DATE_PARAM, LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String totalCaloriesBurned = actions.andReturn().getResponse().getContentAsString();
        assertEquals(ZERO_CALORIES, totalCaloriesBurned);
    }

    @Test
    void getTotalCaloriesBurnedUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + TOTAL_CALORIES_BURNED_ENDPOINT)
                .param(DATE_PARAM, DATE))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void wrongRequest() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "AAA/BBB/CCC"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(errorType(WRONG_REQUEST));
    }
}