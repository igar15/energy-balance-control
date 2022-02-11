package ru.javaprojects.mealservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.repository.MealRepository;
import ru.javaprojects.mealservice.to.MealTo;
import ru.javaprojects.mealservice.util.MessageSender;
import ru.javaprojects.mealservice.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static ru.javaprojects.mealservice.util.MealUtil.createFromTo;
import static ru.javaprojects.mealservice.util.MealUtil.updateFromTo;

@Service
public class MealService {
    private final MealRepository repository;
    private MessageSender messageSender;

    public MealService(MealRepository repository, MessageSender messageSender) {
        this.repository = repository;
        this.messageSender = messageSender;
    }

    public Meal create(MealTo mealTo, long userId) {
        Assert.notNull(mealTo, "mealTo must not be null");
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
        Assert.notNull(mealTo, "mealTo must not be null");
        Meal meal = get(mealTo.getId(), userId);
        updateFromTo(meal, mealTo);
    }

    public void delete(long id, long userId) {
        Meal meal = get(id, userId);
        repository.delete(meal);
    }

    public Page<Meal> getPage(Pageable pageable, long userId) {
        Assert.notNull(pageable, "pageable must not be null");
        return repository.findAllByUserIdOrderByDateTimeDesc(pageable, userId);
    }

    public int getTotalCalories(LocalDate date, long userId) {
        Assert.notNull(date, "date must not be null");
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
        messageSender.sendMessageDateCreated(dateTime.toLocalDate(), userId);
    }

    //Use only for tests
    void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}