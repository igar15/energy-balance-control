package ru.javaprojects.mealservice.testdata;

import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.to.MealTo;

import java.time.LocalDateTime;
import java.time.Month;

import static java.time.LocalDateTime.*;
import static java.time.Month.*;
import static ru.javaprojects.mealservice.model.Meal.*;

public class MealTestData {
    public static final long MEAL1_ID = START_SEQ;
    public static final long USER2_MEAL_ID = START_SEQ + 7;
    public static final long NOT_FOUND = 10;
    public static final long USER1_ID = 200000;
    public static final long USER2_ID = 200001;

    public static final Meal meal1 = new Meal(MEAL1_ID, of(2022, FEBRUARY, 5, 10, 0), "1User Breakfast", 700, USER1_ID);
    public static final Meal meal2 = new Meal(MEAL1_ID + 1, of(2022, FEBRUARY, 5, 13, 0), "1User Lunch", 1000, USER1_ID);
    public static final Meal meal3 = new Meal(MEAL1_ID + 2, of(2022, FEBRUARY, 5, 19, 0), "1User Dinner", 500, USER1_ID);
    public static final Meal meal4 = new Meal(MEAL1_ID + 3, of(2022, FEBRUARY, 6, 9, 30), "1User Breakfast", 400, USER1_ID);
    public static final Meal meal5 = new Meal(MEAL1_ID + 4, of(2022, FEBRUARY, 6, 13, 20), "1User Lunch", 1100, USER1_ID);
    public static final Meal meal6 = new Meal(MEAL1_ID + 5, of(2022, FEBRUARY, 6, 19, 40), "1User Dinner", 600, USER1_ID);
    public static final Meal meal7 = new Meal(MEAL1_ID + 6, of(2022, FEBRUARY, 7, 0, 0), "1User Night Eating", 100, USER1_ID);
    public static final Meal user2Meal1 = new Meal(USER2_MEAL_ID, of(2022, FEBRUARY, 5, 9, 30), "2User Breakfast", 800, USER2_ID);
    public static final Meal user2Meal2 = new Meal(USER2_MEAL_ID + 1, of(2022, FEBRUARY, 5, 13, 20), "2User Lunch", 1500, USER2_ID);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2022, FEBRUARY, 1, 15, 0), "new meal", 600, USER1_ID);
    }

    public static MealTo getNewTo() {
        return new MealTo(null, LocalDateTime.of(2022, FEBRUARY, 1, 15, 0), "new meal", 600);
    }
}
