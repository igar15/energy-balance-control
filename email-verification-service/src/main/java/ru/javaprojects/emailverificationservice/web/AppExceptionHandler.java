package ru.javaprojects.emailverificationservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.javaprojects.emailverificationservice.util.exception.EmailVerificationException;
import ru.javaprojects.energybalancecontrolshared.util.exception.ErrorInfo;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.energybalancecontrolshared.web.BasicAppExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.DATA_NOT_FOUND;
import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.VALIDATION_ERROR;


@RestControllerAdvice
public class AppExceptionHandler extends BasicAppExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> notFoundError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ExceptionHandler(EmailVerificationException.class)
    public ResponseEntity<ErrorInfo> emailVerifyError(HttpServletRequest req, EmailVerificationException e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }
}