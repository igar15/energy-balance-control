package ru.javaprojects.energybalanceservice.web;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.javaprojects.energybalancecontrolshared.web.BasicAppExceptionHandler;


@RestControllerAdvice
public class AppExceptionHandler extends BasicAppExceptionHandler {
}