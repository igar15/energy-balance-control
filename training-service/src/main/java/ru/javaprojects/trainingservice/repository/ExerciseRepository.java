package ru.javaprojects.trainingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Exercise> findAllByExerciseType_UserIdOrderByDateTimeDesc(Pageable pageable, long userId);

    // join with exercise type for userid using
//    @Query(value = "SELECT * FROM exercises WHERE user_id= :userId AND date_time >= :startOfTheDay AND date_time < :endOfTheDay LIMIT 1", nativeQuery = true)
//    Optional<Exercise> findFirstByUserIdAndDate(@Param("startOfTheDay") LocalDateTime startOfTheDay, @Param("endOfTheDay") LocalDateTime endOfTheDay, @Param("userId") long userId);
}