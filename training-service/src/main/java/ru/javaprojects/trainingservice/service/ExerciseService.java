package ru.javaprojects.trainingservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.trainingservice.messaging.MessageSender;
import ru.javaprojects.trainingservice.model.Exercise;
import ru.javaprojects.trainingservice.model.ExerciseType;
import ru.javaprojects.trainingservice.repository.ExerciseRepository;
import ru.javaprojects.trainingservice.to.ExerciseTo;
import ru.javaprojects.trainingservice.util.exception.DateTimeUniqueException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import static ru.javaprojects.trainingservice.util.ExerciseUtil.createFromTo;
import static ru.javaprojects.trainingservice.util.ExerciseUtil.updateFromTo;
import static ru.javaprojects.trainingservice.web.AppExceptionHandler.EXCEPTION_DUPLICATE_DATE_TIME;

@Service
public class ExerciseService {
    private static final String MUST_NOT_BE_NULL = " must not be null";
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
        Assert.notNull(exerciseTo, "exerciseTo" + MUST_NOT_BE_NULL);
        boolean dateExists = checkDateAlreadyExists(exerciseTo.getDateTime(), userId);
        if (dateExists) {
            checkDateTimeOnUnique(userId, exerciseTo.getId(), exerciseTo.getDateTime());
        }
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
        Assert.notNull(exerciseTo, "exerciseTo" + MUST_NOT_BE_NULL);
        checkDateTimeOnUnique(userId, exerciseTo.getId(), exerciseTo.getDateTime());
        Exercise exercise = get(exerciseTo.id(), userId);
        ExerciseType exerciseType = exerciseTypeService.get(exerciseTo.getExerciseTypeId(), userId);
        updateFromTo(exercise, exerciseTo);
        exercise.setExerciseType(exerciseType);
    }

    public void delete(long id, long userId) {
        Exercise exercise = get(id, userId);
        repository.delete(exercise);
    }

    public Page<Exercise> getPage(Pageable pageable, long userId) {
        Assert.notNull(pageable, "pageable" + MUST_NOT_BE_NULL);
        return repository.findAllByExerciseType_UserIdOrderByDateTimeDesc(pageable, userId);
    }

    public int getTotalCaloriesBurned(LocalDate date, long userId) {
        Assert.notNull(date, "date" + MUST_NOT_BE_NULL);
        return repository.getTotalCaloriesBurned(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), userId).orElse(0);
    }

    Exercise get(long id, long userId) {
        return repository.findByIdAndExerciseType_UserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Not found exercise with id=" + id + ", userId=" + userId));
    }

    private void checkDateTimeOnUnique(long userId, Long id, LocalDateTime dateTime) {
        repository.findByExerciseType_UserIdAndDateTime(userId, dateTime)
                .ifPresent(exercise -> {
                    if (!exercise.getId().equals(id)) {
                        throw new DateTimeUniqueException(EXCEPTION_DUPLICATE_DATE_TIME);
                    }
                });
    }

    private boolean checkDateAlreadyExists(LocalDateTime dateTime, long userId) {
        if (dateTime == null) {
            return false;
        }
        LocalDate date = dateTime.toLocalDate();
        return repository.findFirstByUserAndDate(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), userId).isPresent();
    }

    private void sendMessageDateCreated(LocalDateTime dateTime, long userId) {
        if (Objects.nonNull(dateTime)) {
            messageSender.sendDateMessage(dateTime.toLocalDate(), userId);
        }
    }

    //use only for tests
    void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}