package ru.javaprojects.mealservice.testdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.javaprojects.energybalancecontrolshared.test.TestMatcher;
import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.to.MealTo;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.of;
import static java.time.Month.FEBRUARY;
import static ru.javaprojects.mealservice.model.Meal.START_SEQ;

public class MealTestData {
    public static final TestMatcher<Meal> MEAL_MATCHER = TestMatcher.usingIgnoringFieldsComparator(Meal.class, "userId");

    public static final long MEAL1_ID = START_SEQ;
    public static final long ADMIN_MEAL1_ID = START_SEQ + 7;

    public static final Meal meal1 = new Meal(MEAL1_ID, of(2022, FEBRUARY, 5, 10, 0), "1User Breakfast", 700);
    public static final Meal meal2 = new Meal(MEAL1_ID + 1, of(2022, FEBRUARY, 5, 13, 0), "1User Lunch", 1000);
    public static final Meal meal3 = new Meal(MEAL1_ID + 2, of(2022, FEBRUARY, 5, 19, 0), "1User Dinner", 500);
    public static final Meal meal4 = new Meal(MEAL1_ID + 3, of(2022, FEBRUARY, 6, 9, 30), "1User Breakfast", 400);
    public static final Meal meal5 = new Meal(MEAL1_ID + 4, of(2022, FEBRUARY, 6, 13, 20), "1User Lunch", 1100);
    public static final Meal meal6 = new Meal(MEAL1_ID + 5, of(2022, FEBRUARY, 6, 19, 40), "1User Dinner", 600);
    public static final Meal meal7 = new Meal(MEAL1_ID + 6, of(2022, FEBRUARY, 7, 0, 0), "1User Night Eating", 100);

    public static final String INVALID_MEAL_DESCRIPTION = " ";
    public static final int INVALID_MEAL_CALORIES = 6000;

    public static final String DATE = "2022-02-06";
    public static final String TOTAL_CALORIES = "2100";
    public static final String ZERO_CALORIES = "0";

    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "5";
    public static final Pageable PAGEABLE = PageRequest.of(Integer.parseInt(PAGE_NUMBER), Integer.parseInt(PAGE_SIZE));
    public static final Page<Meal> PAGE = new PageImpl<>(List.of(meal7, meal6, meal5, meal4, meal3), PAGEABLE, 7);

    public static final String DATE_PARAM = "date";
    public static final String PAGE_NUMBER_PARAM = "page";
    public static final String PAGE_SIZE_PARAM = "size";

    public static final String TOTAL_CALORIES_ENDPOINT = "total-calories";

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2022, FEBRUARY, 1, 15, 0), "new meal", 600);
    }

    public static MealTo getNewTo() {
        return new MealTo(null, LocalDateTime.of(2022, FEBRUARY, 1, 15, 0), "new meal", 600);
    }

    public static Meal getUpdated() {
        return new Meal(MEAL1_ID, of(2022, FEBRUARY, 3, 14, 30), "Updated description", 400);
    }

    public static MealTo getUpdatedTo() {
        return new MealTo(MEAL1_ID, of(2022, FEBRUARY, 3, 14, 30), "Updated description", 400);
    }
}