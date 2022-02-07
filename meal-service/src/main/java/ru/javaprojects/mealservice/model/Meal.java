package ru.javaprojects.mealservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalTime;

@Entity
@Table(name = "meals", uniqueConstraints = {@UniqueConstraint(columnNames = {"meal_date_id", "time"}, name = "meals_unique_meal_date_time_idx")})
public class Meal extends AbstractBaseEntity {

    @Column(name = "time", nullable = false)
    @NotNull
    private LocalTime time;

    @Column(name = "description", nullable = false)
    @NotBlank
    @Size(min = 2, max = 120)
    private String description;

    @Column(name = "calories", nullable = false)
    @NotNull
    @Range(min = 1, max = 5000)
    private Integer calories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_date_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JsonBackReference
    @NotNull
    private MealDate mealDate;

    public Meal() {
    }

    public Meal(Long id, LocalTime time, String description, Integer calories) {
        super(id);
        this.time = time;
        this.description = description;
        this.calories = calories;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public MealDate getMealDate() {
        return mealDate;
    }

    public void setMealDate(MealDate mealDate) {
        this.mealDate = mealDate;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", time=" + time +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}