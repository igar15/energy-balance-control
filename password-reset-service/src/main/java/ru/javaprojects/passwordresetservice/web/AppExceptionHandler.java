package ru.javaprojects.passwordresetservice.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.javaprojects.energybalancecontrolshared.util.exception.ErrorInfo;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.energybalancecontrolshared.web.BasicAppExceptionHandler;
import ru.javaprojects.passwordresetservice.util.exception.PasswordResetException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.DATA_NOT_FOUND;
import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.VALIDATION_ERROR;


@RestControllerAdvice
public class AppExceptionHandler extends BasicAppExceptionHandler {
    public static final String EXCEPTION_INVALID_PASSWORD = "Password length should be between 5 and 32 characters";
    private static final String INVALID_PASSWORD_CONSTRAINT = "resetPassword.password: size must be between 5 and 32";

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> notFoundError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<ErrorInfo> passwordResetError(HttpServletRequest req, PasswordResetException e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorInfo> validationError(HttpServletRequest req, ConstraintViolationException e) {
        String details = INVALID_PASSWORD_CONSTRAINT.equals(e.getMessage()) ? EXCEPTION_INVALID_PASSWORD : e.getMessage();
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, details);
    }
}