package ru.javaprojects.trainingservice.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.trainingservice.messaging.MessageSender;
import ru.javaprojects.trainingservice.model.Exercise;
import ru.javaprojects.trainingservice.testdata.ExerciseTypeTestData;
import ru.javaprojects.trainingservice.to.ExerciseTo;
import ru.javaprojects.trainingservice.util.exception.DateTimeUniqueException;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static java.time.LocalDateTime.of;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.energybalancecontrolshared.test.TestData.*;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.getNew;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.getNewTo;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.getUpdated;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.getUpdatedTo;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.*;
import static ru.javaprojects.trainingservice.testdata.ExerciseTypeTestData.*;

class ExerciseServiceTest extends AbstractServiceTest {

    @Autowired
    private ExerciseService service;

    @Mock
    private MessageSender messageSender;

    @PostConstruct
    void setupExerciseService() {
        service.setMessageSender(messageSender);
    }

    @Test
    void createWithSendingMessageDateCreated() {
        Exercise created = service.create(getNewTo(), USER_ID);
        long newId = created.id();
        Exercise newExercise = getNew();
        newExercise.setId(newId);
        EXERCISE_MATCHER.assertMatch(created, newExercise);
        Mockito.verify(messageSender, Mockito.times(1)).sendDateMessage(getNewTo().getDateTime().toLocalDate(), USER_ID);
    }

    @Test
    void createWithoutSendingMessageDateCreated() {
        ExerciseTo newTo = getNewTo();
        newTo.setDateTime(of(exercise1.getDateTime().toLocalDate(), LocalTime.of(6, 0)));
        service.create(newTo, USER_ID);
        Mockito.verify(messageSender, Mockito.times(0)).sendDateMessage(Mockito.any(LocalDate.class), Mockito.anyLong());
    }

    @Test
    void duplicateDateTimeCreate() {
        assertThrows(DateTimeUniqueException.class, () -> service.create(new ExerciseTo(null, exercise1.getDateTime(), 100, ExerciseTypeTestData.EXERCISE_TYPE1_ID), USER_ID));
    }

    @Test
    void duplicateDateTimeCreateDifferentUser() {
        assertDoesNotThrow(() -> service.create(new ExerciseTo(null, exercise1.getDateTime(), 100, ADMIN_EXERCISE_TYPE1_ID), ADMIN_ID));
    }

    @Test
    void createInvalid() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTo(null, null, 50, EXERCISE_TYPE1_ID), USER_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTo(null, of(2022, JANUARY, 1, 10, 0), 0, EXERCISE_TYPE1_ID), USER_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTo(null, of(2022, JANUARY, 1, 10, 0), 100001, EXERCISE_TYPE1_ID), USER_ID));
    }

    @Test
    void update() {
        service.update(getUpdatedTo(), USER_ID);
        EXERCISE_MATCHER.assertMatch(service.get(EXERCISE1_ID, USER_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        ExerciseTo updatedTo = getUpdatedTo();
        updatedTo.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updatedTo, USER_ID));
    }

    @Test
    void updateNotOwn() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdatedTo(), ADMIN_ID));
    }

    @Test
    void duplicateDateTimeUpdate() {
        ExerciseTo updatedTo = getUpdatedTo();
        updatedTo.setDateTime(exercise2.getDateTime());
        assertThrows(DateTimeUniqueException.class, () -> service.update(updatedTo, USER_ID));
    }

    @Test
    void duplicateDateTimeUpdateDifferentUser() {
        ExerciseTo updatedTo = getUpdatedTo();
        updatedTo.setId(ADMIN_EXERCISE1_ID);
        updatedTo.setExerciseTypeId(ADMIN_EXERCISE_TYPE1_ID);
        updatedTo.setDateTime(exercise1.getDateTime());
        assertDoesNotThrow(() -> service.update(updatedTo, ADMIN_ID));
    }

    @Test
    void delete() {
        service.delete(EXERCISE1_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(EXERCISE1_ID, USER_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER_ID));
    }

    @Test
    void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> service.delete(EXERCISE1_ID, ADMIN_ID));
    }

    @Test
    void getPage() {
        Page<Exercise> exercisePage = service.getPage(PAGEABLE, USER_ID);
        assertThat(exercisePage).usingRecursiveComparison().ignoringFields("exerciseType").isEqualTo(PAGE);
        List<Exercise> exercises = exercisePage.getContent();
        EXERCISE_TYPE_MATCHER.assertMatch(exercises.get(0).getExerciseType(), exerciseTypeDeleted);
        EXERCISE_TYPE_MATCHER.assertMatch(exercises.get(1).getExerciseType(), exerciseType3);
        EXERCISE_TYPE_MATCHER.assertMatch(exercises.get(2).getExerciseType(), exerciseType1);
    }

    @Test
    void getTotalCaloriesBurned() {
        int totalCaloriesBurned = service.getTotalCaloriesBurned(LocalDate.of(2022, FEBRUARY, 6), USER_ID);
        assertEquals(Integer.parseInt(TOTAL_CALORIES_BURNED), totalCaloriesBurned);
    }

    @Test
    void getTotalCaloriesBurnedWhenNoExercises() {
        int totalCaloriesBurned = service.getTotalCaloriesBurned(LocalDate.of(2022, FEBRUARY, 20), USER_ID);
        assertEquals(Integer.parseInt(ZERO_CALORIES), totalCaloriesBurned);
    }

    @Test
    void get() {
        Exercise exercise = service.get(EXERCISE1_ID, USER_ID);
        EXERCISE_MATCHER.assertMatch(exercise, exercise1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER_ID));
    }

    @Test
    void getNotOwn() {
        assertThrows(NotFoundException.class, () -> service.get(EXERCISE1_ID, ADMIN_ID));
    }
}