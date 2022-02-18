package ru.javaprojects.trainingservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.trainingservice.TestUtil;
import ru.javaprojects.trainingservice.model.ExerciseType;
import ru.javaprojects.trainingservice.repository.ExerciseTypeRepository;
import ru.javaprojects.trainingservice.to.ExerciseTypeTo;
import ru.javaprojects.trainingservice.web.json.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.trainingservice.testdata.ExerciseTypeTestData.*;
import static ru.javaprojects.trainingservice.testdata.UserTestData.*;
import static ru.javaprojects.trainingservice.util.exception.ErrorType.*;
import static ru.javaprojects.trainingservice.web.AppExceptionHandler.EXCEPTION_DUPLICATE_DESCRIPTION;
import static ru.javaprojects.trainingservice.web.AppExceptionHandler.EXCEPTION_NOT_AUTHORIZED;

class ExerciseTypeControllerTest extends AbstractControllerTest {
    private static final String REST_URL = ExerciseTypeController.REST_URL + '/';

    @Autowired
    private ExerciseTypeRepository repository;

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EXERCISE_TYPE_MATCHER.contentJson(exerciseType1, exerciseType2, exerciseType3));
    }

    @Test
    void getAllUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void create() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewTo())))
                .andExpect(status().isCreated());
        ExerciseType created = TestUtil.readFromJson(actions, ExerciseType.class);
        long newId = created.id();
        ExerciseType newExerciseType = getNew();
        newExerciseType.setId(newId);
        EXERCISE_TYPE_MATCHER.assertMatch(created, newExerciseType);
        EXERCISE_TYPE_MATCHER.assertMatch(repository.findByIdAndUserId(newId, USER1_ID).get(), newExerciseType);
    }

    @Test
    void createUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewTo())))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        ExerciseTypeTo newTo = getNewTo();
        newTo.setDescription(exerciseType1.getDescription());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_DESCRIPTION));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void createInvalid() throws Exception {
        ExerciseTypeTo newTo = getNewTo();
        newTo.setDescription(INVALID_DESCRIPTION);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE_TYPE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedTo())))
                .andExpect(status().isNoContent());
        EXERCISE_TYPE_MATCHER.assertMatch(repository.findByIdAndUserId(EXERCISE_TYPE1_ID, USER1_ID).get(), getUpdated());
    }

    @Test
    void updateUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE_TYPE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedTo())))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void updateIdNotConsistent() throws Exception {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setId(EXERCISE_TYPE1_ID + 1);
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE_TYPE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void updateNotFound() throws Exception {
        ExerciseTypeTo updatedTo = getUpdatedTo();
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
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE_TYPE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue( getUpdatedTo())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setDescription(exerciseType2.getDescription());
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE_TYPE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_DESCRIPTION));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void updateInvalid() throws Exception {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setCaloriesBurned(INVALID_CALORIES_BURNED);
        perform(MockMvcRequestBuilders.put(REST_URL + EXERCISE_TYPE1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }


    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + EXERCISE_TYPE1_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(repository.findByIdAndUserId(EXERCISE_TYPE1_ID, USER1_ID).get().isDeleted());
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
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + EXERCISE_TYPE1_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void deleteUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + EXERCISE_TYPE1_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }
}