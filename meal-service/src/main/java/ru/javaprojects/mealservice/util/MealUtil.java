package ru.javaprojects.mealservice.util;

import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.to.MealTo;

public class MealUtil {
    private MealUtil() {
    }

    public static Meal createFromTo(MealTo mealTo) {
        return new Meal(mealTo.getId(), mealTo.getDateTime(), mealTo.getDescription(), mealTo.getCalories());
    }

    public static Meal updateFromTo(Meal meal, MealTo mealTo) {
        meal.setDateTime(mealTo.getDateTime());
        meal.setDescription(mealTo.getDescription());
        meal.setCalories(mealTo.getCalories());
        return meal;
    }
}