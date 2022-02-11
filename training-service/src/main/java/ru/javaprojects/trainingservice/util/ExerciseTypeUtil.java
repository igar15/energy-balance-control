package ru.javaprojects.trainingservice.util;

import ru.javaprojects.trainingservice.model.ExerciseType;
import ru.javaprojects.trainingservice.to.ExerciseTypeTo;

public class ExerciseTypeUtil {
    private ExerciseTypeUtil() {
    }

    public static ExerciseType createFromTo(ExerciseTypeTo exerciseTypeTo) {
        return new ExerciseType(exerciseTypeTo.getId(), exerciseTypeTo.getDescription(), exerciseTypeTo.getMeasure(), exerciseTypeTo.getCaloriesBurned());
    }

    public static ExerciseType updateFromTo(ExerciseType exerciseType, ExerciseTypeTo exerciseTypeTo) {
        exerciseType.setDescription(exerciseTypeTo.getDescription());
        exerciseType.setMeasure(exerciseTypeTo.getMeasure());
        exerciseType.setCaloriesBurned(exerciseTypeTo.getCaloriesBurned());
        return exerciseType;
    }
}