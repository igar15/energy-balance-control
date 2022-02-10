package ru.javaprojects.trainingservice.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "exercises")
public class Exercise extends AbstractBaseEntity {

    @Column(name = "date_time", nullable = false)
    @NotNull
    private LocalDateTime dateTime;

    @Column(name = "amount", nullable = false)
    @NotNull
    @Range(min = 1, max = 100000)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_type_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ExerciseType exerciseType;

    public Exercise() {
    }

    public Exercise(Long id, LocalDateTime dateTime, Integer amount) {
        super(id);
        this.dateTime = dateTime;
        this.amount = amount;
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

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                "dateTime=" + dateTime +
                ", amount=" + amount +
                '}';
    }
}