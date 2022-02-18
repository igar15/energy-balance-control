package ru.javaprojects.mealservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.mealservice.messaging.MessageSender;
import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.repository.MealRepository;
import ru.javaprojects.mealservice.to.MealTo;
import ru.javaprojects.mealservice.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import static ru.javaprojects.mealservice.util.MealUtil.createFromTo;
import static ru.javaprojects.mealservice.util.MealUtil.updateFromTo;

@Service
public class MealService {
    private static final String MUST_NOT_BE_NULL = " must not be null";
    private final MealRepository repository;
    private MessageSender messageSender;

    public MealService(MealRepository repository, MessageSender messageSender) {
        this.repository = repository;
        this.messageSender = messageSender;
    }

    public Meal create(MealTo mealTo, long userId) {
        Assert.notNull(mealTo, "mealTo" + MUST_NOT_BE_NULL);
        boolean dateExists = checkDateAlreadyExists(mealTo.getDateTime(), userId);
        Meal meal = createFromTo(mealTo);
        meal.setUserId(userId);
        repository.save(meal);
        if (!dateExists) {
            sendMessageDateCreated(mealTo.getDateTime(), userId);
        }
        return meal;
    }

    @Transactional
    public void update(MealTo mealTo, long userId) {
        Assert.notNull(mealTo, "mealTo" + MUST_NOT_BE_NULL);
        Meal meal = get(mealTo.id(), userId);
        updateFromTo(meal, mealTo);
    }

    public void delete(long id, long userId) {
        Meal meal = get(id, userId);
        repository.delete(meal);
    }

    @Transactional
    public void deleteAll(long userId) {
       repository.deleteAllByUser(userId);
    }

    public Page<Meal> getPage(Pageable pageable, long userId) {
        Assert.notNull(pageable, "pageable" + MUST_NOT_BE_NULL);
        return repository.findAllByUserIdOrderByDateTimeDesc(pageable, userId);
    }

    public int getTotalCalories(LocalDate date, long userId) {
        Assert.notNull(date, "date" + MUST_NOT_BE_NULL);
        return repository.getTotalCalories(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), userId).orElse(0);
    }

    Meal get(long id, long userId) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Not found meal with id=" + id + ", userId=" + userId));
    }

    private boolean checkDateAlreadyExists(LocalDateTime dateTime, long userId) {
        if (dateTime == null) {
            return false;
        }
        LocalDate date = dateTime.toLocalDate();
        return repository.findFirstByUserIdAndDate(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), userId).isPresent();
    }

    private void sendMessageDateCreated(LocalDateTime dateTime, long userId) {
        if (Objects.nonNull(dateTime)) {
            messageSender.sendDateCreatedMessage(dateTime.toLocalDate(), userId);
        }
    }

    //Use only for tests
    void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}