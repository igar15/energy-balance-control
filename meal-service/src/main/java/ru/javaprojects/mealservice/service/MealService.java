package ru.javaprojects.mealservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.repository.MealRepository;
import ru.javaprojects.mealservice.to.MealTo;
import ru.javaprojects.mealservice.util.NotFoundException;

import java.time.LocalDate;

import static ru.javaprojects.mealservice.util.MealUtil.createFromTo;
import static ru.javaprojects.mealservice.util.MealUtil.updateFromTo;

@Service
public class MealService {
    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(MealTo mealTo, long userId) {
        Assert.notNull(mealTo, "mealTo must not be null");
        boolean dateExists = checkDateAlreadyExists(mealTo.getDateTime().toLocalDate(), userId);
        Meal meal = createFromTo(mealTo);
        meal.setUserId(userId);
        repository.save(meal);
        if (!dateExists) {
            //TODO: SEND MESSAGE TO QUEUE TO CREATE BX FOR CURRENT DATE
        }
        return meal;
    }

    @Transactional
    public void update(MealTo mealTo, long userId) {
        Assert.notNull(mealTo, "mealTo must not be null");
        Meal meal = repository.findByIdAndUserId(mealTo.getId(), userId).orElseThrow(() -> new NotFoundException("Not found meal with id=" + mealTo.getId() + ", userId=" + userId));
        updateFromTo(meal, mealTo);
    }

    public void delete(long id, long userId) {
        Meal meal = repository.findByIdAndUserId(id, userId).orElseThrow(() -> new NotFoundException("Not found meal with id=" + id + ", userId=" + userId));
        repository.delete(meal);
    }

    public Page<Meal> getAll(Pageable pageable, long userId) {
        Assert.notNull(pageable, "pageable must not be null");
        return repository.findAllByUserIdOrderByDateTimeDesc(pageable, userId);
    }

    public int getTotalCalories(LocalDate date, long userId) {
        Assert.notNull(date, "date must not be null");
        return repository.getTotalCalories(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), userId).orElse(0);
    }

    private boolean checkDateAlreadyExists(LocalDate date, long userId) {
        return repository.findFirstByUserIdAndDate(date.atStartOfDay(), date.plusDays(1).atStartOfDay(), userId).isPresent();
    }
}
