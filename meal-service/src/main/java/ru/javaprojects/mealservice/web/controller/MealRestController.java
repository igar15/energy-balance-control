package ru.javaprojects.mealservice.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javaprojects.energybalancecontrolshared.web.security.SecurityUtil;
import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.service.MealService;
import ru.javaprojects.mealservice.to.MealTo;

import javax.validation.Valid;
import java.time.LocalDate;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;
import static ru.javaprojects.energybalancecontrolshared.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.energybalancecontrolshared.util.ValidationUtil.checkNew;


@RestController
@RequestMapping(value = MealRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Meal Rest Controller")
public class MealRestController {
    static final String REST_URL = "/api/meals";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(description = "Get meal page")
    @Parameters({@Parameter(name = "page", in = QUERY, description = "Zero-based page index (0..N)", schema = @Schema(type = "integer", defaultValue = "0")),
                 @Parameter(name = "size", in = QUERY, description = "The size of the page to be returned", schema = @Schema(type = "integer", defaultValue = "20"))})
    public Page<Meal> getPage(@Parameter(hidden = true) Pageable pageable) {
        long userId = SecurityUtil.authUserId();
        log.info("getPage(pageNumber={}, pageSize={}) for user {}", pageable.getPageNumber(), pageable.getPageSize(), userId);
        return service.getPage(pageable, userId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Create new meal")
    public Meal create(@Valid @RequestBody MealTo mealTo) {
        long userId = SecurityUtil.authUserId();
        log.info("create {} for user {}", mealTo, userId);
        checkNew(mealTo);
        return service.create(mealTo, userId);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Update meal")
    public void update(@Valid @RequestBody MealTo mealTo, @PathVariable long id) {
        long userId = SecurityUtil.authUserId();
        log.info("update {} for user {}", mealTo, userId);
        assureIdConsistent(mealTo, id);
        service.update(mealTo, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Delete meal")
    public void delete(@PathVariable long id) {
        long userId = SecurityUtil.authUserId();
        log.info("delete meal {} for user {}", id, userId);
        service.delete(id, userId);
    }

    @GetMapping("/total-calories")
    @Operation(description = "Get total calories by date")
    public Integer getTotalCalories(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        long userId = SecurityUtil.authUserId();
        log.info("getTotalCalories for date {} for user {}", date, userId);
        return service.getTotalCalories(date, userId);
    }
}