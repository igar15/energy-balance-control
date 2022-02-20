package ru.javaprojects.userservice.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.javaprojects.energybalancecontrolshared.util.ValidationUtil;
import ru.javaprojects.energybalancecontrolshared.util.exception.ErrorInfo;
import ru.javaprojects.energybalancecontrolshared.util.exception.IllegalRequestDataException;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.energybalancecontrolshared.web.BasicAppExceptionHandler;
import ru.javaprojects.userservice.util.exception.EmailVerificationException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.*;
import static ru.javaprojects.energybalancecontrolshared.web.security.SecurityConstants.*;


@RestControllerAdvice
public class AppExceptionHandler extends BasicAppExceptionHandler {
    public static final String EXCEPTION_DUPLICATE_EMAIL = "User with this email already exists";
    public static final String EXCEPTION_INVALID_PASSWORD = "Password length should be between 5 and 32 characters";
    public static final String USER_EMAIL_CONSTRAIN = "users_unique_email_idx";
    private static final String INVALID_PASSWORD_CONSTRAINT = "changePassword.password: size must be between 5 and 32";

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> notFoundError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorInfo> conflictError(HttpServletRequest req, DataIntegrityViolationException e) {
        String rootMsg = ValidationUtil.getRootCause(e).getMessage();
        if (rootMsg != null) {
            String lowerCaseMsg = rootMsg.toLowerCase();
            if (lowerCaseMsg.contains(USER_EMAIL_CONSTRAIN)) {
                return logAndGetErrorInfo(req, e, false, DATA_ERROR, EXCEPTION_DUPLICATE_EMAIL);
            }
        }
        return logAndGetErrorInfo(req, e, true, DATA_ERROR);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorInfo> bindValidationError(HttpServletRequest req, BindException e) {
        String[] details = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
                .toArray(String[]::new);
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorInfo> validationError(HttpServletRequest req, ConstraintViolationException e) {
        String details = INVALID_PASSWORD_CONSTRAINT.equals(e.getMessage()) ? EXCEPTION_INVALID_PASSWORD : e.getMessage();
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, details);
    }

    @ExceptionHandler({IllegalRequestDataException.class, EmailVerificationException.class})
    public ResponseEntity<ErrorInfo> invalidRequestDataError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorInfo> forbiddenRequestError(HttpServletRequest req, AccessDeniedException e) {
        return logAndGetErrorInfo(req, e, false, ACCESS_DENIED_ERROR, ACCESS_DENIED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorInfo> badCredentialsError(HttpServletRequest req, BadCredentialsException e) {
        return logAndGetErrorInfo(req, e, false, BAD_CREDENTIALS_ERROR, BAD_CREDENTIALS);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorInfo> disabledError(HttpServletRequest req, DisabledException e) {
        return logAndGetErrorInfo(req, e, false, DISABLED_ERROR, DISABLED);
    }
}