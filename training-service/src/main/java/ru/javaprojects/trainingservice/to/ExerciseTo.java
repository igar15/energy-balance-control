package ru.javaprojects.trainingservice.to;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ExerciseTo extends BaseTo {

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    @Range(min = 1, max = 100000)
    private Integer amount;

    @NotNull
    private Long exerciseTypeId;

    public ExerciseTo() {
    }

    public ExerciseTo(Long id, LocalDateTime dateTime, Integer amount, Long exerciseTypeId) {
        super(id);
        this.dateTime = dateTime;
        this.amount = amount;
        this.exerciseTypeId = exerciseTypeId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getExerciseTypeId() {
        return exerciseTypeId;
    }

    public void setExerciseTypeId(Long exerciseTypeId) {
        this.exerciseTypeId = exerciseTypeId;
    }

    @Override
    public String toString() {
        return "ExerciseTo{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", amount=" + amount +
                ", exerciseTypeId=" + exerciseTypeId +
                '}';
    }
}