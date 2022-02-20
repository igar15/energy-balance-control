package ru.javaprojects.trainingservice.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javaprojects.energybalancecontrolshared.web.security.SecurityUtil;
import ru.javaprojects.trainingservice.model.ExerciseType;
import ru.javaprojects.trainingservice.service.ExerciseTypeService;
import ru.javaprojects.trainingservice.to.ExerciseTypeTo;

import javax.validation.Valid;
import java.util.List;

import static ru.javaprojects.energybalancecontrolshared.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.energybalancecontrolshared.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = ExerciseTypeController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ExerciseTypeController {
    static final String REST_URL = "/api/exercise-types";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ExerciseTypeService service;

    public ExerciseTypeController(ExerciseTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<ExerciseType> getAll() {
        long userId = SecurityUtil.authUserId();
        log.info("getAll for user {}", userId);
        return service.getAll(userId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseType create(@Valid @RequestBody ExerciseTypeTo exerciseTypeTo) {
        long userId = SecurityUtil.authUserId();
        log.info("create {} for user {}", exerciseTypeTo, userId);
        checkNew(exerciseTypeTo);
        return service.create(exerciseTypeTo, userId);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody ExerciseTypeTo exerciseTypeTo, @PathVariable long id) {
        long userId = SecurityUtil.authUserId();
        log.info("update {} for user {}", exerciseTypeTo, userId);
        assureIdConsistent(exerciseTypeTo, id);
        service.update(exerciseTypeTo, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        long userId = SecurityUtil.authUserId();
        log.info("delete exerciseType {} for user {}", id, userId);
        service.delete(id, userId);
    }
}