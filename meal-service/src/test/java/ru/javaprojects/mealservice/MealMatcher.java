package ru.javaprojects.mealservice;

import org.springframework.test.web.servlet.ResultMatcher;
import ru.javaprojects.mealservice.model.Meal;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MealMatcher {
    private MealMatcher() {
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public ResultMatcher contentJson(Meal expected) {
        return result -> assertMatch(TestUtil.readFromJsonMvcResult(result, Meal.class), expected);
    }

    public final ResultMatcher contentJson(Meal... expected) {
        return contentJson(List.of(expected));
    }

    public ResultMatcher contentJson(Iterable<Meal> expected) {
        return result -> assertMatch(TestUtil.readListFromJsonMvcResult(result, Meal.class), expected);
    }
}