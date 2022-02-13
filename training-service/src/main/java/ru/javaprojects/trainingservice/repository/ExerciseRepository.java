package ru.javaprojects.trainingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.trainingservice.model.Exercise;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    Optional<Exercise> findByIdAndExerciseType_UserId(long id, long userId);

    @EntityGraph(attributePaths = {"exerciseType"})
    Page<Exercise> findAllByExerciseType_UserIdOrderByDateTimeDesc(Pageable pageable, long userId);

    Optional<Exercise> findByExerciseType_UserIdAndDateTime(long userId, LocalDateTime dateTime);

    @Query(value = "SELECT * FROM exercises JOIN exercise_types ON exercises.exercise_type_id = exercise_types.id" +
            " WHERE user_id= :userId AND date_time >= :startOfTheDay AND date_time < :endOfTheDay LIMIT 1", nativeQuery = true)
    Optional<Exercise> findFirstByUserAndDate(@Param("startOfTheDay") LocalDateTime startOfTheDay,
                                              @Param("endOfTheDay") LocalDateTime endOfTheDay, @Param("userId") long userId);

    @Query(value = "SELECT SUM(calories_burned * amount) FROM exercises JOIN exercise_types ON exercises.exercise_type_id = exercise_types.id" +
            " WHERE user_id= :userId AND date_time >= :startOfTheDay AND date_time < :endOfTheDay", nativeQuery = true)
    Optional<Integer> getTotalCaloriesBurned(@Param("startOfTheDay") LocalDateTime startOfTheDay,
                                             @Param("endOfTheDay") LocalDateTime endOfTheDay, @Param("userId") long userId);
}