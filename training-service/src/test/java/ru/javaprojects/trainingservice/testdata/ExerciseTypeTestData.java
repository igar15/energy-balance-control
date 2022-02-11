package ru.javaprojects.trainingservice.testdata;

import ru.javaprojects.trainingservice.TestMatcher;
import ru.javaprojects.trainingservice.model.ExerciseType;
import ru.javaprojects.trainingservice.to.ExerciseTypeTo;

import static ru.javaprojects.trainingservice.model.AbstractBaseEntity.START_SEQ;

public class ExerciseTypeTestData {
    public static final TestMatcher<ExerciseType> EXERCISE_TYPE_MATCHER = TestMatcher.usingIgnoringFieldsComparator(ExerciseType.class, "userId");

    public static final long EXERCISE_TYPE1_ID = START_SEQ;
    public static final long USER2_EXERCISE_TYPE1_ID = START_SEQ + 4;
    public static final long NOT_FOUND = 10;

    public static final ExerciseType exerciseType1 = new ExerciseType(EXERCISE_TYPE1_ID, "1User Exercise Type 1", "times", 2, false);
    public static final ExerciseType exerciseType2 = new ExerciseType(EXERCISE_TYPE1_ID + 1, "1User Exercise Type 2", "meters", 3, false);
    public static final ExerciseType exerciseType3 = new ExerciseType(EXERCISE_TYPE1_ID + 2, "1User Exercise Type 3", "times", 1, false);
    public static final ExerciseType exerciseTypeDeleted = new ExerciseType(EXERCISE_TYPE1_ID + 3, "1User Exercise Type Deleted", "times", 2, true);

    public static ExerciseType getNew() {
        return new ExerciseType(null, "new exercise type", "times", 3, false);
    }

    public static ExerciseTypeTo getNewTo() {
        return new ExerciseTypeTo(null, "new exercise type", "times", 3);
    }

    public static ExerciseType getUpdated() {
        return new ExerciseType(EXERCISE_TYPE1_ID, "updated description", "updated measure", 5, false);
    }

    public static ExerciseTypeTo getUpdatedTo() {
        return new ExerciseTypeTo(EXERCISE_TYPE1_ID, "updated description", "updated measure", 5);
    }
}