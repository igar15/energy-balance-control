package ru.javaprojects.trainingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "exercise_types")
public class ExerciseType extends AbstractBaseEntity {

    @Column(name = "description", nullable = false)
    @NotBlank
    @Size(min = 2, max = 120)
    private String description;

    @Column(name = "measure", nullable = false)
    @NotBlank
    @Size(min = 2, max = 40)
    private String measure;

    @Column(name = "calories_burned", nullable = false)
    @NotNull
    @Range(min = 1, max = 1000)
    private Integer caloriesBurned;

    @Column(name = "deleted", nullable = false, columnDefinition = "bool default false")
    private boolean deleted = false;

    @Column(name = "user_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Long userId;

    public ExerciseType() {
    }

    public ExerciseType(Long id, String description, String measure, Integer caloriesBurned) {
        super(id);
        this.description = description;
        this.measure = measure;
        this.caloriesBurned = caloriesBurned;
    }

    public ExerciseType(Long id, String description, String measure, Integer caloriesBurned, boolean deleted) {
        super(id);
        this.description = description;
        this.measure = measure;
        this.caloriesBurned = caloriesBurned;
        this.deleted = deleted;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ExerciseType{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", measure='" + measure + '\'' +
                ", caloriesBurned=" + caloriesBurned +
                ", deleted=" + deleted +
                ", userId=" + userId +
                '}';
    }
}