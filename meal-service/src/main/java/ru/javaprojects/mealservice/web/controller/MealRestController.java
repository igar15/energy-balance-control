package ru.javaprojects.mealservice.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javaprojects.mealservice.model.Meal;
import ru.javaprojects.mealservice.service.MealService;
import ru.javaprojects.mealservice.to.MealTo;
import ru.javaprojects.mealservice.web.security.JwtProvider;
import ru.javaprojects.mealservice.web.security.SecurityUtil;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;

import static ru.javaprojects.mealservice.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.mealservice.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = MealRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MealRestController {
    static final String REST_URL = "/api/meals";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    // ----REMOVE THIS LATER----->>>

    @Autowired
    private JwtProvider jwtProvider;

    @GetMapping("/token")
    @ResponseStatus(HttpStatus.OK)
    public void createAuthToken(HttpServletResponse response) {
        String token = jwtProvider.generateAuthorizationToken("200000","ROLE_USER");
        response.addHeader("token", token);
    }

    // ----REMOVE THIS LATER-----<<<<

    @GetMapping
    public Page<Meal> getPage(Pageable pageable) {
        long userId = SecurityUtil.authUserId();
        log.info("getPage(pageNumber={}, pageSize={}) for user {}", pageable.getPageNumber(), pageable.getPageSize(), userId);
        return service.getPage(pageable, userId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Meal create(@Valid @RequestBody MealTo mealTo) {
        long userId = SecurityUtil.authUserId();
        log.info("create {} for user {}", mealTo, userId);
        checkNew(mealTo);
        return service.create(mealTo, userId);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody MealTo mealTo, @PathVariable long id) {
        long userId = SecurityUtil.authUserId();
        log.info("update {} for user {}", mealTo, userId);
        assureIdConsistent(mealTo, id);
        service.update(mealTo, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        long userId = SecurityUtil.authUserId();
        log.info("delete meal {} for user {}", id, userId);
        service.delete(id, userId);
    }

    @GetMapping("/total-calories")
    public Integer getTotalCalories(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        long userId = SecurityUtil.authUserId();
        log.info("getTotalCalories for date {} for user {}", date, userId);
        return service.getTotalCalories(date, userId);
    }
}