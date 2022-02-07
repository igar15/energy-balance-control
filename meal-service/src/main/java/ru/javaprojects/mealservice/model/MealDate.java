package ru.javaprojects.mealservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "meal_dates", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date"}, name = "meal_dates_unique_user_date_idx")})
public class MealDate extends AbstractBaseEntity {

    @Column(name = "date", nullable = false)
    @NotNull
    private LocalDate date;

    @Column(name = "user_id", nullable = false)
    @NotNull
    private Long userId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mealDate")
    @OrderBy("time DESC")
//    @JsonManagedReference
    private List<Meal> meals;

    public MealDate() {
    }

    public MealDate(Long id, LocalDate date, Long userId) {
        super(id);
        this.date = date;
        this.userId = userId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    @Override
    public String toString() {
        return "MealDate{" +
                "id=" + id +
                ", date=" + date +
                ", userId=" + userId +
                '}';
    }
}