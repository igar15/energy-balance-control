package ru.javaprojects.mealservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.javaprojects.mealservice.model.Meal;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
}
