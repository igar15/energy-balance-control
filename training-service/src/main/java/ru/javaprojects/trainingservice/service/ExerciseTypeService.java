package ru.javaprojects.trainingservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.trainingservice.model.ExerciseType;
import ru.javaprojects.trainingservice.repository.ExerciseTypeRepository;
import ru.javaprojects.trainingservice.to.ExerciseTypeTo;
import ru.javaprojects.trainingservice.util.exception.NotFoundException;

import java.util.List;

import static ru.javaprojects.trainingservice.util.ExerciseTypeUtil.createFromTo;
import static ru.javaprojects.trainingservice.util.ExerciseTypeUtil.updateFromTo;

@Service
public class ExerciseTypeService {
    private final ExerciseTypeRepository repository;

    public ExerciseTypeService(ExerciseTypeRepository repository) {
        this.repository = repository;
    }

    public List<ExerciseType> getAll(long userId) {
        return repository.findAllByUserIdAndDeletedFalseOrderByDescription(userId);
    }

    public ExerciseType create(ExerciseTypeTo exerciseTypeTo, long userId) {
        Assert.notNull(exerciseTypeTo, "exerciseTypeTo must not be null");
        ExerciseType exerciseType = createFromTo(exerciseTypeTo);
        exerciseType.setUserId(userId);
        return repository.save(exerciseType);
    }

    @Transactional
    public void update(ExerciseTypeTo exerciseTypeTo, long userId) {
        Assert.notNull(exerciseTypeTo, "exerciseTypeTo must not be null");
        ExerciseType exerciseType = get(exerciseTypeTo.getId(), userId);
        updateFromTo(exerciseType, exerciseTypeTo);
    }

    @Transactional
    public void delete(long id, long userId) {
        ExerciseType exerciseType = get(id, userId);
        exerciseType.setDeleted(true);
    }

    ExerciseType get(long id, long userId) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Not found exerciseType with id=" + id + ", userId=" + userId));
    }
}