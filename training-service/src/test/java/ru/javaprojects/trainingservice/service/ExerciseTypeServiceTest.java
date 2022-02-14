package ru.javaprojects.trainingservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javaprojects.trainingservice.model.ExerciseType;
import ru.javaprojects.trainingservice.to.ExerciseTypeTo;
import ru.javaprojects.trainingservice.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.trainingservice.testdata.ExerciseTypeTestData.*;
import static ru.javaprojects.trainingservice.testdata.UserTestData.USER1_ID;
import static ru.javaprojects.trainingservice.testdata.UserTestData.USER2_ID;

class ExerciseTypeServiceTest extends AbstractServiceTest {

    @Autowired
    private ExerciseTypeService service;

    @Test
    void getAll() {
        List<ExerciseType> exerciseTypes = service.getAll(USER1_ID);
        EXERCISE_TYPE_MATCHER.assertMatch(exerciseTypes, exerciseType1, exerciseType2, exerciseType3);
    }

    @Test
    void create() {
        ExerciseType created = service.create(getNewTo(), USER1_ID);
        long newId = created.id();
        ExerciseType newExerciseType = getNew();
        newExerciseType.setId(newId);
        EXERCISE_TYPE_MATCHER.assertMatch(created, newExerciseType);
    }

    @Test
    void duplicateDescriptionCreate() {
        assertThrows(DataAccessException.class,
                () -> service.create(new ExerciseTypeTo(null, exerciseType1.getDescription(), "seconds", 1), USER1_ID));
    }

    @Test
    void duplicateDescriptionCreateWhenDeletedTrue() {
        assertDoesNotThrow(() -> service.create(new ExerciseTypeTo(null, exerciseTypeDeleted.getDescription(), "seconds", 1), USER1_ID));
    }

    @Test
    void duplicateDescriptionCreateDifferentUser() {
        assertDoesNotThrow(() -> service.create(new ExerciseTypeTo(null, exerciseType1.getDescription(), "seconds", 1), USER2_ID));
    }

    @Test
    void createInvalid() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTypeTo(null, " ", "times", 3), USER1_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTypeTo(null, "description", " ", 3), USER1_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTypeTo(null, "description", "times", 0), USER1_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTypeTo(null, "description", "times", 1001), USER1_ID));
    }

    @Test
    void update() {
        service.update(getUpdatedTo(), USER1_ID);
        EXERCISE_TYPE_MATCHER.assertMatch(service.get(EXERCISE_TYPE1_ID, USER1_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updatedTo, USER1_ID));
    }

    @Test
    void updateNotOwn() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdatedTo(), USER2_ID));
    }

    @Test
    void duplicateDescriptionUpdate() {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setDescription(exerciseType2.getDescription());
        assertThrows(DataAccessException.class, () -> service.update(updatedTo, USER1_ID));
    }

    @Test
    void duplicateDescriptionUpdateWhenDeletedTrue() {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setDescription(exerciseTypeDeleted.getDescription());
        assertDoesNotThrow(() -> service.update(updatedTo, USER1_ID));
    }

    @Test
    void duplicateDescriptionUpdateDifferentUser() {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setId(USER2_EXERCISE_TYPE1_ID);
        updatedTo.setDescription(exerciseType1.getDescription());
        assertDoesNotThrow(() -> service.update(updatedTo, USER2_ID));
    }

    @Test
    void delete() {
        service.delete(EXERCISE_TYPE1_ID, USER1_ID);
        assertTrue(service.get(EXERCISE_TYPE1_ID, USER1_ID).isDeleted());
    }

    @Test
    void deleteWhenDescriptionDeletedTrueExists() {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setDescription(exerciseTypeDeleted.getDescription());
        service.update(updatedTo, USER1_ID);
        assertDoesNotThrow(() -> service.delete(EXERCISE_TYPE1_ID, USER1_ID));
        assertTrue(service.get(EXERCISE_TYPE1_ID, USER1_ID).isDeleted());
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER1_ID));
    }

    @Test
    void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> service.delete(EXERCISE_TYPE1_ID, USER2_ID));
    }

    @Test
    void get() {
        ExerciseType exerciseType = service.get(EXERCISE_TYPE1_ID, USER1_ID);
        EXERCISE_TYPE_MATCHER.assertMatch(exerciseType, exerciseType1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER1_ID));
    }

    @Test
    void getNotOwn() {
        assertThrows(NotFoundException.class, () -> service.get(EXERCISE_TYPE1_ID, USER2_ID));
    }
}