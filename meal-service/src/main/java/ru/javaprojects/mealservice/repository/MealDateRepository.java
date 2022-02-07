package ru.javaprojects.mealservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.javaprojects.mealservice.model.MealDate;

@Repository
public interface MealDateRepository extends JpaRepository<MealDate, Long> {
}
