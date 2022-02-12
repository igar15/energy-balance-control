package ru.javaprojects.trainingservice.web.json;

import org.junit.jupiter.api.Test;
import ru.javaprojects.trainingservice.model.Exercise;

import java.util.List;

import static ru.javaprojects.trainingservice.testdata.ExerciseTestData.*;

class JsonUtilTest {

    @Test
    void readWriteValue() {
        String json = JsonUtil.writeValue(exercise1);
        System.out.println(json);
        Exercise exercise = JsonUtil.readValue(json, Exercise.class);
        EXERCISE_MATCHER.assertMatch(exercise, exercise1);
    }

    @Test
    void readWriteValues() {
        String json = JsonUtil.writeValue(List.of(exercise1, exercise2, exercise3));
        System.out.println(json);
        List<Exercise> exercises = JsonUtil.readValues(json, Exercise.class);
        EXERCISE_MATCHER.assertMatch(exercises, List.of(exercise1, exercise2, exercise3));
    }

    @Test
    void readContentFromPage() {
        List<Exercise> exercises = JsonUtil.readContentFromPage(JSON_EXERCISE_PAGE, Exercise.class);
        EXERCISE_MATCHER.assertMatch(exercises, exercise6, exercise5, exercise4);
    }
}