package ru.javaprojects.mealservice.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.javaprojects.mealservice.MealMatcher;
import ru.javaprojects.mealservice.messaging.MessageSender;
import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.repository.MealRepository;
import ru.javaprojects.mealservice.to.MealTo;
import ru.javaprojects.mealservice.util.ValidationUtil;
import ru.javaprojects.mealservice.util.exception.NotFoundException;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalTime;

import static java.time.LocalDateTime.of;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.mealservice.testdata.MealTestData.*;

@SpringBootTest
@ActiveProfiles("dev")
@Sql(scripts = "classpath:data.sql", config = @SqlConfig(encoding = "UTF-8"))
class MealServiceTest {

    @Autowired
    private MealService service;

    @Autowired
    private MealRepository repository;

    @Mock
    private MessageSender messageSender;

    @PostConstruct
    void setupMealService() {
        service.setMessageSender(messageSender);
    }

    @Test
    void createWithSendingMessageDateCreated() {
        Meal created = service.create(getNewTo(), USER1_ID);
        long newId = created.id();
        Meal newMeal = getNew();
        newMeal.setId(newId);
        MealMatcher.assertMatch(created, newMeal);
        Mockito.verify(messageSender, Mockito.times(1)).sendDateCreatedMessage(getNewTo().getDateTime().toLocalDate(), USER1_ID);

    }

    @Test
    void createWithoutSendingMessageDateCreated() {
        MealTo newTo = getNewTo();
        newTo.setDateTime(of(meal1.getDateTime().toLocalDate(), LocalTime.of(6, 0)));
        service.create(newTo, USER1_ID);
        Mockito.verify(messageSender, Mockito.times(0)).sendDateCreatedMessage(Mockito.any(LocalDate.class), Mockito.anyLong());
    }

    @Test
    void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () -> service.create(new MealTo(null, meal1.getDateTime(), "duplicate", 250), USER1_ID));
    }

    @Test
    void duplicateDateTimeCreateDifferentUser() {
        assertDoesNotThrow(() -> service.create(new MealTo(null, meal1.getDateTime(), "duplicate", 250), USER2_ID));
    }

    @Test
    void createInvalid() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new MealTo(null, of(2022, JANUARY, 1, 10, 0), " ", 300), USER1_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new MealTo(null, null, "Description", 300), USER1_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new MealTo(null, of(2022, JANUARY, 1, 10, 0), "Description", 0), USER1_ID));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new MealTo(null, of(2022, JANUARY, 1, 10, 0), "Description", 5001), USER1_ID));
    }

    @Test
    void update() {
        service.update(getUpdatedTo(), USER1_ID);
        MealMatcher.assertMatch(service.get(MEAL1_ID, USER1_ID), getUpdated());
    }

    @Test
    void updateNotFound() {
        MealTo updatedTo = getUpdatedTo();
        updatedTo.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updatedTo, USER1_ID));
    }

    @Test
    void updateNotOwn() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdatedTo(), USER2_ID));
    }

    @Test
    void duplicateDateTimeUpdate() {
        MealTo updatedTo = getUpdatedTo();
        updatedTo.setDateTime(meal2.getDateTime());
        assertThrows(DataAccessException.class, () -> service.update(updatedTo, USER1_ID));
    }

    @Test
    void duplicateDateTimeUpdateDifferentUser() {
        MealTo updatedTo = getUpdatedTo();
        updatedTo.setId(USER2_MEAL1_ID);
        updatedTo.setDateTime(meal1.getDateTime());
        assertDoesNotThrow(() -> service.update(updatedTo, USER2_ID));
    }

    @Test
    void delete() {
        service.delete(MEAL1_ID, USER1_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL1_ID, USER1_ID));
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND, USER1_ID));
    }

    @Test
    void deleteNotOwn() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL1_ID, USER2_ID));
    }

    @Test
    void deleteAll() {
        service.deleteAll(USER1_ID);
        assertTrue(repository.findAllByUserId(USER1_ID).isEmpty());
        assertFalse(repository.findAllByUserId(USER2_ID).isEmpty());

    }

    @Test
    void getPage() {
        Page<Meal> mealPage = service.getPage(PAGEABLE, USER1_ID);
        assertThat(mealPage).usingRecursiveComparison().ignoringFields("userId").isEqualTo(PAGE);
        MealMatcher.assertMatch(mealPage.getContent(), meal7, meal6, meal5, meal4, meal3);
    }

    @Test
    void getTotalCalories() {
        int totalCalories = service.getTotalCalories(LocalDate.of(2022, FEBRUARY, 6), USER1_ID);
        assertEquals(Integer.parseInt(TOTAL_CALORIES), totalCalories);
    }

    @Test
    void getTotalCaloriesWhenNoMeals() {
        int totalCalories = service.getTotalCalories(LocalDate.of(2022, FEBRUARY, 20), USER1_ID);
        assertEquals(Integer.parseInt(ZERO_CALORIES), totalCalories);
    }

    @Test
    void get() {
        Meal meal = service.get(MEAL1_ID, USER1_ID);
        MealMatcher.assertMatch(meal, meal1);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, USER1_ID));
    }

    @Test
    void getNotOwn() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL1_ID, USER2_ID));
    }

    private <T extends Throwable> void validateRootCause(Class<T> rootExceptionClass, Runnable runnable) {
        assertThrows(rootExceptionClass, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw ValidationUtil.getRootCause(e);
            }
        });
    }
}