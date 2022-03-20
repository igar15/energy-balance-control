package ru.javaprojects.passwordresetservice.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.javaprojects.passwordresetservice.service.PasswordResetService;

import javax.validation.constraints.Size;

@RestController
@RequestMapping(value = PasswordResetRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "Password Reset Rest Controller")
public class PasswordResetRestController {
    static final String REST_URL = "/api/password/reset";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final PasswordResetService service;

    public PasswordResetRestController(PasswordResetService service) {
        this.service = service;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Check password reset token")
    public void checkToken(@RequestParam String token) {
        log.info("check token {}", token);
        service.checkToken(token);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Reset password")
    public void resetPassword(@RequestParam String token, @RequestParam @Size(min = 5, max = 32) String password) {
        log.info("reset password by token {}", token);
        service.resetPassword(token, password);
    }
}