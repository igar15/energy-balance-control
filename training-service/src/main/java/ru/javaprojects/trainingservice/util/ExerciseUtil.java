package ru.javaprojects.trainingservice.util;

import ru.javaprojects.trainingservice.model.Exercise;
import ru.javaprojects.trainingservice.to.ExerciseTo;
import ru.javaprojects.trainingservice.to.ExerciseTypeTo;

public class ExerciseUtil {
    private ExerciseUtil() {
    }

    public static Exercise createFromTo(ExerciseTo exerciseTo) {
        return new Exercise(exerciseTo.getId(), exerciseTo.getDateTime(), exerciseTo.getAmount());
    }

    public static Exercise updateFromTo(Exercise exercise, ExerciseTo exerciseTo) {
        exercise.setDateTime(exerciseTo.getDateTime());
        exercise.setAmount(exerciseTo.getAmount());
        return exercise;
    }
}