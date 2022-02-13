package ru.javaprojects.bxservice.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.javaprojects.bxservice.security.SecurityUtil;
import ru.javaprojects.bxservice.service.BasicExchangeService;

import java.time.LocalDate;

@RestController
@RequestMapping(value = BasicExchangeRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class BasicExchangeRestController {
    static final String REST_URL = "/api/bx";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final BasicExchangeService service;

    public BasicExchangeRestController(BasicExchangeService service) {
        this.service = service;
    }

    @GetMapping("/calories")
    public Integer getBxCalories(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        long userId = SecurityUtil.authUserId();
        log.info("getBxCalories for date {} for user {}", date, userId);
        return service.getBxCalories(date, userId);
    }
}