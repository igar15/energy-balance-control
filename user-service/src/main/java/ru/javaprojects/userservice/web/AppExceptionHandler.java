package ru.javaprojects.userservice.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.javaprojects.userservice.util.ValidationUtil;
import ru.javaprojects.userservice.util.exception.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import static ru.javaprojects.userservice.util.exception.ErrorType.*;


@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class AppExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(AppExceptionHandler.class);

    public static final String EXCEPTION_DUPLICATE_EMAIL = "User with this email already exists";
    public static final String EXCEPTION_INVALID_PASSWORD = "Password length should be between 5 and 32 characters";
    public static final String EXCEPTION_NOT_AUTHORIZED = "You are not authorized";
    public static final String EXCEPTION_ACCESS_DENIED = "You do not have enough permission";
    public static final String EXCEPTION_BAD_CREDENTIALS = "Email / password incorrect. Please try again";
    public static final String EXCEPTION_BAD_TOKEN = "Auth token is invalid. Try to authorize";
    public static final String EXCEPTION_DISABLED = "Your account was disabled";

    public static final String USER_EMAIL_CONSTRAIN = "users_unique_email_idx";
    private static final String INVALID_PASSWORD_CONSTRAINT = "changePassword.password: size must be between 5 and 32";

    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorInfo> wrongRequest(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, WRONG_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> handleError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorInfo> conflict(HttpServletRequest req, DataIntegrityViolationException e) {
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

    @ExceptionHandler({IllegalRequestDataException.class, EmailVerificationException.class,
                       MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorInfo> illegalRequestDataError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorInfo> forbiddenRequestError(HttpServletRequest req, AccessDeniedException e) {
        return logAndGetErrorInfo(req, e, false, ACCESS_DENIED_ERROR, EXCEPTION_ACCESS_DENIED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorInfo> forbiddenRequestError(HttpServletRequest req, BadCredentialsException e) {
        return logAndGetErrorInfo(req, e, false, BAD_CREDENTIALS_ERROR, EXCEPTION_BAD_CREDENTIALS);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorInfo> forbiddenRequestError(HttpServletRequest req, DisabledException e) {
        return logAndGetErrorInfo(req, e, false, DISABLED_ERROR, EXCEPTION_DISABLED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, true, APP_ERROR);
    }

    //    https://stackoverflow.com/questions/538870/should-private-helper-methods-be-static-if-they-can-be-static
    private ResponseEntity<ErrorInfo> logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logStackTrace, ErrorType errorType, String... details) {
        Throwable rootCause = ValidationUtil.logAndGetRootCause(log, req, e, logStackTrace, errorType);
        return ResponseEntity.status(errorType.getStatus())
                .body(new ErrorInfo(req.getRequestURL(), errorType, errorType.getErrorCode(),
                        details.length != 0 ? details : new String[]{ValidationUtil.getMessage(rootCause)})
                );
    }
}