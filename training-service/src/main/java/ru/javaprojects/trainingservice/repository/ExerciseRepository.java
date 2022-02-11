package ru.javaprojects.trainingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.trainingservice.model.Exercise;

@Repository
@Transactional(readOnly = true)
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}