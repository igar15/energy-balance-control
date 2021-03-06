package ru.javaprojects.emailverificationservice.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.javaprojects.emailverificationservice.service.EmailVerificationService;

@RestController
@RequestMapping(value = EmailVerificationRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Email Verification Rest Controller")
public class EmailVerificationRestController {
    static final String REST_URL = "/api/email/verify";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final EmailVerificationService service;

    public EmailVerificationRestController(EmailVerificationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Verify email by token")
    public void verifyEmail(@RequestParam String token) {
        log.info("verify email with token {}", token);
        service.verifyEmail(token);
    }
}