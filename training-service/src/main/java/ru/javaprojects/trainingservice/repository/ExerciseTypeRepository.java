package ru.javaprojects.trainingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.trainingservice.model.ExerciseType;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Long> {

    List<ExerciseType> findAllByUserIdAndDeletedFalseOrderByDescription(long userId);

    Optional<ExerciseType> findByIdAndUserId(long id, long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ExerciseType e WHERE e.userId = :userId")
    void deleteAllByUser(@Param("userId") long userId);

    List<ExerciseType> findAllByUserId(long userId);
}