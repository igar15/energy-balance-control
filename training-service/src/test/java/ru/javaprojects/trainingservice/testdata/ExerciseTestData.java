package ru.javaprojects.trainingservice.testdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.javaprojects.energybalancecontrolshared.test.TestMatcher;
import ru.javaprojects.trainingservice.model.Exercise;
import ru.javaprojects.trainingservice.to.ExerciseTo;

import java.util.List;

import static java.time.LocalDateTime.of;
import static java.time.Month.FEBRUARY;
import static ru.javaprojects.trainingservice.model.AbstractBaseEntity.START_SEQ;
import static ru.javaprojects.trainingservice.testdata.ExerciseTypeTestData.EXERCISE_TYPE1_ID;

public class ExerciseTestData {
    public static final TestMatcher<Exercise> EXERCISE_MATCHER = TestMatcher.usingIgnoringFieldsComparator(Exercise.class, "exerciseType");

    public static final long EXERCISE1_ID = START_SEQ + 5;
    public static final long ADMIN_EXERCISE1_ID = START_SEQ + 11;

    public static final Exercise exercise1 = new Exercise(EXERCISE1_ID, of(2022, FEBRUARY, 5, 11, 20), 30);
    public static final Exercise exercise2 = new Exercise(EXERCISE1_ID + 1, of(2022, FEBRUARY, 5, 13, 50), 100);
    public static final Exercise exercise3 = new Exercise(EXERCISE1_ID + 2, of(2022, FEBRUARY, 5, 15, 15), 20);
    public static final Exercise exercise4 = new Exercise(EXERCISE1_ID + 3, of(2022, FEBRUARY, 6, 14, 15), 50);
    public static final Exercise exercise5 = new Exercise(EXERCISE1_ID + 4, of(2022, FEBRUARY, 6, 18, 15), 20);
    public static final Exercise exercise6 = new Exercise(EXERCISE1_ID + 5, of(2022, FEBRUARY, 6, 20, 10), 35);

    public static final String DATE = "2022-02-06";
    public static final String TOTAL_CALORIES_BURNED = "190";
    public static final String ZERO_CALORIES = "0";

    public static final int INVALID_AMOUNT = 100001;

    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "3";
    public static final Pageable PAGEABLE = PageRequest.of(Integer.parseInt(PAGE_NUMBER), Integer.parseInt(PAGE_SIZE));
    public static final Page<Exercise> PAGE = new PageImpl<>(List.of(exercise6, exercise5, exercise4), PAGEABLE, 6);

    public static final String DATE_PARAM = "date";
    public static final String PAGE_NUMBER_PARAM = "page";
    public static final String PAGE_SIZE_PARAM = "size";

    public static final String TOTAL_CALORIES_BURNED_ENDPOINT = "total-calories-burned";

    public static Exercise getNew() {
        return new Exercise(null, of(2022, FEBRUARY, 4, 7, 0), 50);
    }

    public static ExerciseTo getNewTo() {
        return new ExerciseTo(null, of(2022, FEBRUARY, 4, 7, 0), 50, EXERCISE_TYPE1_ID);
    }

    public static Exercise getUpdated() {
        return new Exercise(EXERCISE1_ID, of(2022, FEBRUARY, 4, 5, 0), 40);
    }

    public static ExerciseTo getUpdatedTo() {
        return new ExerciseTo(EXERCISE1_ID, of(2022, FEBRUARY, 4, 5, 0), 40, EXERCISE_TYPE1_ID);
    }
}