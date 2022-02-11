package ru.javaprojects.mealservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.mealservice.model.Meal;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface MealRepository extends JpaRepository<Meal, Long> {
    Optional<Meal> findByIdAndUserId(long id, long userId);

    Page<Meal> findAllByUserIdOrderByDateTimeDesc(Pageable pageable, long userId);

    @Query(value = "SELECT SUM(calories) FROM meals WHERE user_id= :userId AND date_time >= :startOfTheDay AND date_time < :endOfTheDay", nativeQuery = true)
    Optional<Integer> getTotalCalories(@Param("startOfTheDay") LocalDateTime startOfTheDay,
                                       @Param("endOfTheDay") LocalDateTime endOfTheDay, @Param("userId") long userId);

    @Query(value = "SELECT * FROM meals WHERE user_id= :userId AND date_time >= :startOfTheDay AND date_time < :endOfTheDay LIMIT 1", nativeQuery = true)
    Optional<Meal> findFirstByUserIdAndDate(@Param("startOfTheDay") LocalDateTime startOfTheDay,
                                            @Param("endOfTheDay") LocalDateTime endOfTheDay, @Param("userId") long userId);
}
