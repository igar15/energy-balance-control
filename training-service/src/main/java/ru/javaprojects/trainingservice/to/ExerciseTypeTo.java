package ru.javaprojects.trainingservice.to;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ExerciseTypeTo extends BaseTo {

    @NotBlank
    @Size(min = 2, max = 120)
    private String description;

    @NotBlank
    @Size(min = 2, max = 40)
    private String measure;

    @NotNull
    @Range(min = 1, max = 1000)
    private Integer caloriesBurned;

    public ExerciseTypeTo() {
    }

    public ExerciseTypeTo(Long id, String description, String measure, Integer caloriesBurned) {
        super(id);
        this.description = description;
        this.measure = measure;
        this.caloriesBurned = caloriesBurned;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public Integer getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(Integer caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    @Override
    public String toString() {
        return "ExerciseTypeTo{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", measure='" + measure + '\'' +
                ", caloriesBurned=" + caloriesBurned +
                '}';
    }
}