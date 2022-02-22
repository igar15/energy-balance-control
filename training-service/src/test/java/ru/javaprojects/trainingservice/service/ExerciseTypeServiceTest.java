package ru.javaprojects.trainingservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.trainingservice.model.ExerciseType;
import ru.javaprojects.trainingservice.repository.ExerciseRepository;
import ru.javaprojects.trainingservice.repository.ExerciseTypeRepository;
import ru.javaprojects.trainingservice.to.ExerciseTypeTo;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.energybalancecontrolshared.test.TestData.*;
import static ru.javaprojects.trainingservice.testdata.ExerciseTypeTestData.*;

class ExerciseTypeServiceTest extends AbstractServiceTest {

    @Autowired
    private ExerciseTypeService service;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseTypeRepository repository;

    @Test
    void getAll() {
        List<ExerciseType> exerciseTypes = service.getAll(USER_ID);
        EXERCISE_TYPE_MATCHER.assertMatch(exerciseTypes, exerciseType1, exerciseType2, exerciseType3);
    }

    @Test
    void create() {
        ExerciseType created = service.create(getNewTo(), USER_ID);
        long newId = created.id();
        ExerciseType newExerciseType = getNew();
        newExerciseType.setId(newId);
        EXERCISE_TYPE_MATCHER.assertMatch(created, newExerciseType);
    }

    @Test
    void duplicateDescriptionCreate() {
        assertThrows(DataAccessException.class,
                () -> service.create(new ExerciseTypeTo(null, exerciseType1.getDescription(), SECONDS_MEASURE, 1), USER_ID));
    }

    @Test
    void duplicateDescriptionCreateWhenDeletedTrue() {
        assertDoesNotThrow(() -> service.create(new ExerciseTypeTo(null, exerciseTypeDeleted.getDescription(), SECONDS_MEASURE, 1), USER_ID));
    }

    @Test
    void duplicateDescriptionCreateDifferentUser() {
        assertDoesNotThrow(() -> service.create(new ExerciseTypeTo(null, exerciseType1.getDescription(), SECONDS_MEASURE, 1), ADMIN_ID));
    }

    @Test
    void createInvalid() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTypeTo(null, " ", "times", 3), USER_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTypeTo(null, "description", " ", 3), USER_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTypeTo(null, "description", "times", 0), USER_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTypeTo(null, "description", "times", 1001), USER_ID));
    }

    @Test
    void update() {
        service.update(getUpdatedTo(), USER_ID);
        EXERCISE_TYPE_MATCHER.assertMatch(service.get(EXERCISE_TYPE1_ID, USER_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updatedTo, USER_ID));
    }

    @Test
    void updateNotOwn() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdatedTo(), ADMIN_ID));
    }

    @Test
    void duplicateDescriptionUpdate() {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setDescription(exerciseType2.getDescription());
        assertThrows(DataAccessException.class, () -> service.update(updatedTo, USER_ID));
    }

    @Test
    void duplicateDescriptionUpdateWhenDeletedTrue() {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setDescription(exerciseTypeDeleted.getDescription());
        assertDoesNotThrow(() -> service.update(updatedTo, USER_ID));
    }

    @Test
    void duplicateDescriptionUpdateDifferentUser() {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setId(ADMIN_EXERCISE_TYPE1_ID);
        updatedTo.setDescription(exerciseType1.getDescription());
        assertDoesNotThrow(() -> service.update(updatedTo, ADMIN_ID));
    }

    @Test
    void delete() {
        service.delete(EXERCISE_TYPE1_ID, USER_ID);
        assertTrue(service.get(EXERCISE_TYPE1_ID, USER_ID).isDeleted());
    }

    @Test
    void deleteWhenDescriptionDeletedTrueExists() {
        ExerciseTypeTo updatedTo = getUpdatedTo();
        updatedTo.setDescription(exerciseTypeDeleted.getDescription());
        service.update(updatedTo, USER_ID);
        assertDoesNotThrow(() -> service.delete(EXERCISE_TYPE1_ID, USER_ID));
        assertTrue(service.get(EXERCISE_TYPE1_ID, USER_ID).isDeleted());
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> service.delete(EXERCISE_TYPE1_ID, ADMIN_ID));
    }

    @Test
    void deleteAll() {
        service.deleteAll(USER_ID);
        assertTrue(repository.findAllByUserId(USER_ID).isEmpty());
        assertFalse(repository.findAllByUserId(ADMIN_ID).isEmpty());
        assertTrue(exerciseRepository.findAllByExerciseType_UserId(USER_ID).isEmpty());
        assertFalse(exerciseRepository.findAllByExerciseType_UserId(ADMIN_ID).isEmpty());
    }

    @Test
    void get() {
        ExerciseType exerciseType = service.get(EXERCISE_TYPE1_ID, USER_ID);
        EXERCISE_TYPE_MATCHER.assertMatch(exerciseType, exerciseType1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    void getNotOwn() {
        assertThrows(NotFoundException.class, () -> service.get(EXERCISE_TYPE1_ID, ADMIN_ID));
    }
}