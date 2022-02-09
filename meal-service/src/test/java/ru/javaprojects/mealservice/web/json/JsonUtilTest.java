package ru.javaprojects.mealservice.web.json;

import org.junit.jupiter.api.Test;
import ru.javaprojects.mealservice.MealMatcher;
import ru.javaprojects.mealservice.model.Meal;

import java.util.List;

import static ru.javaprojects.mealservice.testdata.MealTestData.*;

class JsonUtilTest {

    @Test
    void readWriteValue() {
        String json = JsonUtil.writeValue(meal1);
        System.out.println(json);
        Meal meal = JsonUtil.readValue(json, Meal.class);
        MealMatcher.assertMatch(meal, meal1);
    }

    @Test
    void readWriteValues() {
        String json = JsonUtil.writeValue(List.of(meal1, meal2, meal3));
        System.out.println(json);
        List<Meal> meals = JsonUtil.readValues(json, Meal.class);
        MealMatcher.assertMatch(meals, List.of(meal1, meal2, meal3));
    }

    @Test
    void readContentFromPage() {
        List<Meal> meals = JsonUtil.readContentFromPage(JSON_MEAL_PAGE, Meal.class);
        MealMatcher.assertMatch(meals, meal7, meal6, meal5, meal4, meal3);
    }
}