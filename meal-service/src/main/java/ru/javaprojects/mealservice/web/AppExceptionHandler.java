package ru.javaprojects.mealservice.web;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class AppExceptionHandler {
    public static final String EXCEPTION_BAD_TOKEN = "Auth token is invalid. Try to authorize";
}
