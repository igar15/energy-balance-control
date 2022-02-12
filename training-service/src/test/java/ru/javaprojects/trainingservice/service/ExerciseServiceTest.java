package ru.javaprojects.trainingservice.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import ru.javaprojects.trainingservice.model.Exercise;
import ru.javaprojects.trainingservice.testdata.ExerciseTypeTestData;
import ru.javaprojects.trainingservice.to.ExerciseTo;
import ru.javaprojects.trainingservice.util.MessageSender;
import ru.javaprojects.trainingservice.util.exception.DateTimeUniqueException;
import ru.javaprojects.trainingservice.util.exception.NotFoundException;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static java.time.LocalDateTime.of;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.NOT_FOUND;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.getNew;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.getNewTo;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.getUpdated;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.getUpdatedTo;
import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.*;
import static ru.javaprojects.trainingservice.testdata.ExerciseTypeTestData.*;
import static ru.javaprojects.trainingservice.testdata.UserTestData.USER1_ID;
import static ru.javaprojects.trainingservice.testdata.UserTestData.USER2_ID;

class ExerciseServiceTest extends AbstractServiceTest {

    @Autowired
    private ExerciseService service;

    @Mock
    private MessageSender messageSender;

    @Test
    void create() {
        Exercise created = service.create(getNewTo(), USER1_ID);
        long newId = created.id();
        Exercise newExercise = getNew();
        newExercise.setId(newId);
        EXERCISE_MATCHER.assertMatch(created, newExercise);
    }

    @Test
    void createWithSendingMessageDateCreated() {
        service.setMessageSender(messageSender);
        service.create(getNewTo(), USER1_ID);
        Mockito.verify(messageSender, Mockito.times(1)).sendMessageDateCreated(getNewTo().getDateTime().toLocalDate(), USER1_ID);
    }

    @Test
    void createWithoutSendingMessageDateCreated() {
        service.setMessageSender(messageSender);
        ExerciseTo newTo = getNewTo();
        newTo.setDateTime(of(exercise1.getDateTime().toLocalDate(), LocalTime.of(6, 0)));
        service.create(newTo, USER1_ID);
        Mockito.verify(messageSender, Mockito.times(0)).sendMessageDateCreated(newTo.getDateTime().toLocalDate(), USER1_ID);
    }

    @Test
    void duplicateDateTimeCreate() {
        assertThrows(DateTimeUniqueException.class, () -> service.create(new ExerciseTo(null, exercise1.getDateTime(), 100, ExerciseTypeTestData.EXERCISE_TYPE1_ID), USER1_ID));
    }

    @Test
    void duplicateDateTimeCreateDifferentUser() {
        assertDoesNotThrow(() -> service.create(new ExerciseTo(null, exercise1.getDateTime(), 100, USER2_EXERCISE_TYPE1_ID), USER2_ID));
    }

    @Test
    void createInvalid() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTo(null, null, 50, EXERCISE_TYPE1_ID), USER1_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTo(null, of(2022, JANUARY, 1, 10, 0), 0, EXERCISE_TYPE1_ID), USER1_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new ExerciseTo(null, of(2022, JANUARY, 1, 10, 0), 100001, EXERCISE_TYPE1_ID), USER1_ID));
    }

    @Test
    void update() {
        service.update(getUpdatedTo(), USER1_ID);
        EXERCISE_MATCHER.assertMatch(service.get(EXERCISE1_ID, USER1_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        ExerciseTo updatedTo = getUpdatedTo();
        updatedTo.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updatedTo, USER1_ID));
    }

    @Test
    void updateNotOwn() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdatedTo(), USER2_ID));
    }

    @Test
    void duplicateDateTimeUpdate() {
        ExerciseTo updatedTo = getUpdatedTo();
        updatedTo.setDateTime(exercise2.getDateTime());
        assertThrows(DateTimeUniqueException.class, () -> service.update(updatedTo, USER1_ID));
    }

    @Test
    void duplicateDateTimeUpdateDifferentUser() {
        ExerciseTo updatedTo = getUpdatedTo();
        updatedTo.setId(USER2_EXERCISE1_ID);
        updatedTo.setExerciseTypeId(USER2_EXERCISE_TYPE1_ID);
        updatedTo.setDateTime(exercise1.getDateTime());
        assertDoesNotThrow(() -> service.update(updatedTo, USER2_ID));
    }

    @Test
    void delete() {
        service.delete(EXERCISE1_ID, USER1_ID);
        assertThrows(NotFoundException.class, () -> service.get(EXERCISE1_ID, USER1_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER1_ID));
    }

    @Test
    void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> service.delete(EXERCISE1_ID, USER2_ID));
    }

    @Test
    void getPage() {
        Page<Exercise> exercisePage = service.getPage(PAGEABLE, USER1_ID);
        assertThat(exercisePage).usingRecursiveComparison().ignoringFields("exerciseType").isEqualTo(PAGE);
        List<Exercise> exercises = exercisePage.getContent();
        assertThat(exercises.get(0).getExerciseType()).usingRecursiveComparison().ignoringFields("userId").isEqualTo(exerciseTypeDeleted);
        assertThat(exercises.get(1).getExerciseType()).usingRecursiveComparison().ignoringFields("userId").isEqualTo(exerciseType3);
        assertThat(exercises.get(2).getExerciseType()).usingRecursiveComparison().ignoringFields("userId").isEqualTo(exerciseType1);
    }

    @Test
    void getTotalCaloriesBurned() {
        int totalCaloriesBurned = service.getTotalCaloriesBurned(LocalDate.of(2022, FEBRUARY, 6), USER1_ID);
        assertEquals(Integer.parseInt(TOTAL_CALORIES_BURNED), totalCaloriesBurned);
    }

    @Test
    void getTotalCaloriesBurnedWhenNoExercises() {
        int totalCaloriesBurned = service.getTotalCaloriesBurned(LocalDate.of(2022, FEBRUARY, 20), USER1_ID);
        assertEquals(Integer.parseInt(ZERO_CALORIES), totalCaloriesBurned);
    }

    @Test
    void get() {
        Exercise exercise = service.get(EXERCISE1_ID, USER1_ID);
        EXERCISE_MATCHER.assertMatch(exercise, exercise1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER1_ID));
    }

    @Test
    void getNotOwn() {
        assertThrows(NotFoundException.class, () -> service.get(EXERCISE1_ID, USER2_ID));
    }
}