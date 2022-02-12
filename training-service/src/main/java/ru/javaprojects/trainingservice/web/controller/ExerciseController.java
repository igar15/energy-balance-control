package ru.javaprojects.trainingservice.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javaprojects.trainingservice.model.Exercise;
import ru.javaprojects.trainingservice.service.ExerciseService;
import ru.javaprojects.trainingservice.to.ExerciseTo;
import ru.javaprojects.trainingservice.web.security.SecurityUtil;

import javax.validation.Valid;
import java.time.LocalDate;

import static ru.javaprojects.trainingservice.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.trainingservice.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = ExerciseController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ExerciseController {
    static final String REST_URL = "/api/exercises";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ExerciseService service;

    public ExerciseController(ExerciseService service) {
        this.service = service;
    }

    @GetMapping
    public Page<Exercise> getPage(Pageable pageable) {
        long userId = SecurityUtil.authUserId();
        log.info("getPage(pageNumber={}, pageSize={}) for user {}", pageable.getPageNumber(), pageable.getPageSize(), userId);
        return service.getPage(pageable, userId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Exercise create(@Valid @RequestBody ExerciseTo exerciseTo) {
        long userId = SecurityUtil.authUserId();
        log.info("create {} for user {}", exerciseTo, userId);
        checkNew(exerciseTo);
        return service.create(exerciseTo, userId);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody ExerciseTo exerciseTo, @PathVariable long id) {
        long userId = SecurityUtil.authUserId();
        log.info("update {} for user {}", exerciseTo, userId);
        assureIdConsistent(exerciseTo, id);
        service.update(exerciseTo, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        long userId = SecurityUtil.authUserId();
        log.info("delete exercise {} for user {}", id, userId);
        service.delete(id, userId);
    }

    @GetMapping("/total-calories-burned")
    public Integer getTotalCaloriesBurned(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        long userId = SecurityUtil.authUserId();
        log.info("getTotalCaloriesBurned for date {} for user {}", date, userId);
        return service.getTotalCaloriesBurned(date, userId);
    }
}