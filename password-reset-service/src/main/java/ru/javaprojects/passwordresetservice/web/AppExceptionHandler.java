package ru.javaprojects.passwordresetservice.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.javaprojects.passwordresetservice.util.ValidationUtil;
import ru.javaprojects.passwordresetservice.util.exception.ErrorInfo;
import ru.javaprojects.passwordresetservice.util.exception.ErrorType;
import ru.javaprojects.passwordresetservice.util.exception.NotFoundException;
import ru.javaprojects.passwordresetservice.util.exception.PasswordResetException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import static ru.javaprojects.passwordresetservice.util.exception.ErrorType.*;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class AppExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(AppExceptionHandler.class);

    public static final String EXCEPTION_INVALID_PASSWORD = "Password length should be between 5 and 32 characters";

    private static final String INVALID_PASSWORD_CONSTRAINT = "resetPassword.password: size must be between 5 and 32";

    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorInfo> wrongRequest(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, WRONG_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> handleError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<ErrorInfo> handleError(HttpServletRequest req, PasswordResetException e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorInfo> validationError(HttpServletRequest req, ConstraintViolationException e) {
        String details = INVALID_PASSWORD_CONSTRAINT.equals(e.getMessage()) ? EXCEPTION_INVALID_PASSWORD : e.getMessage();
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, details);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
                       HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorInfo> illegalRequestDataError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
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