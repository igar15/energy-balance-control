package ru.javaprojects.trainingservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.trainingservice.model.Exercise;
import ru.javaprojects.trainingservice.model.ExerciseType;
import ru.javaprojects.trainingservice.repository.ExerciseRepository;
import ru.javaprojects.trainingservice.to.ExerciseTo;
import ru.javaprojects.trainingservice.util.MessageSender;
import ru.javaprojects.trainingservice.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static ru.javaprojects.trainingservice.util.ExerciseUtil.createFromTo;
import static ru.javaprojects.trainingservice.util.ExerciseUtil.updateFromTo;

@Service
public class ExerciseService {

    private final ExerciseRepository repository;
    private final ExerciseTypeService exerciseTypeService;
    private MessageSender messageSender;

    public ExerciseService(ExerciseRepository repository, ExerciseTypeService exerciseTypeService, MessageSender messageSender) {
        this.repository = repository;
        this.exerciseTypeService = exerciseTypeService;
        this.messageSender = messageSender;
    }

    @Transactional
    public Exercise create(ExerciseTo exerciseTo, long userId) {
        Assert.notNull(exerciseTo, "exerciseTo must not be null");
        boolean dateExists = checkDateAlreadyExists(exerciseTo.getDateTime(), userId);
        ExerciseType exerciseType = exerciseTypeService.get(exerciseTo.getExerciseTypeId(), userId);
        Exercise exercise = createFromTo(exerciseTo);
        exercise.setExerciseType(exerciseType);
        repository.save(exercise);
        if (!dateExists) {
            sendMessageDateCreated(exerciseTo.getDateTime(), userId);
        }
        return exercise;
    }

    @Transactional
    public void update(ExerciseTo exerciseTo, long userId) {
        Assert.notNull(exerciseTo, "exerciseTo must not be null");
        Exercise exercise = get(exerciseTo.getId(), userId);
        ExerciseType exerciseType = exerciseTypeService.get(exerciseTo.getExerciseTypeId(), userId);
        updateFromTo(exercise, exerciseTo);
        exercise.setExerciseType(exerciseType);
    }

    public void delete(long id, long userId) {
        Exercise exercise = get(id, userId);
        repository.delete(exercise);
    }

    public Page<Exercise> getPage(Pageable pageable, long userId) {
        Assert.notNull(pageable, "pageable must not be null");
        return repository.findAllByUserIdOrderByDateTimeDesc(pageable, userId);
    }

    // CHECK ALL FOR USING USER ID
    // IN CREATE AND UPDATE PROGRAMMATICALLY CHECK LOCAL DATE TIME ON UNIQUE

    Exercise get(long id, long userId) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Not found exercise with id=" + id + ", userId=" + userId));
    }

    private boolean checkDateAlreadyExists(LocalDateTime dateTime, long userId) {
        if (dateTime == null) {
            return false;
        }
        LocalDate date = dateTime.toLocalDate();
        return repository.findFirstByUserIdAndDate(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), userId).isPresent();
    }

    private void sendMessageDateCreated(LocalDateTime dateTime, long userId) {
        messageSender.sendMessageDateCreated(dateTime.toLocalDate(), userId);
    }
}