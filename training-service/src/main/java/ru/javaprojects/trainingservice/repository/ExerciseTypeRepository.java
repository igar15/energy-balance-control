package ru.javaprojects.trainingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.trainingservice.model.ExerciseType;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface ExerciseTypeRepository extends JpaRepository<ExerciseType, Long> {

    List<ExerciseType> findAllByUserIdAndDeletedFalse(long userId);

    Optional<ExerciseType> findByIdAndUserId(long id, long userId);
}