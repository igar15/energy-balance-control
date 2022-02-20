package ru.javaprojects.trainingservice.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.javaprojects.energybalancecontrolshared.util.ValidationUtil;
import ru.javaprojects.energybalancecontrolshared.util.exception.ErrorInfo;
import ru.javaprojects.energybalancecontrolshared.util.exception.IllegalRequestDataException;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.energybalancecontrolshared.web.BasicAppExceptionHandler;
import ru.javaprojects.trainingservice.util.exception.DateTimeUniqueException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.*;


@RestControllerAdvice
public class AppExceptionHandler extends BasicAppExceptionHandler {
    public static final String EXCEPTION_DUPLICATE_DESCRIPTION = "Exercise type with this description already exists";
    public static final String EXCEPTION_DUPLICATE_DATE_TIME = "Exercise with this date and time already exists";
    public static final String EXERCISE_TYPE_DESCRIPTION_CONSTRAIN = "exercise_types_unique_user_description_idx";


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> notFoundError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorInfo> conflictError(HttpServletRequest req, DataIntegrityViolationException e) {
        String rootMsg = ValidationUtil.getRootCause(e).getMessage();
        if (rootMsg != null) {
            String lowerCaseMsg = rootMsg.toLowerCase();
            if (lowerCaseMsg.contains(EXERCISE_TYPE_DESCRIPTION_CONSTRAIN)) {
                return logAndGetErrorInfo(req, e, false, DATA_ERROR, EXCEPTION_DUPLICATE_DESCRIPTION);
            }
        }
        return logAndGetErrorInfo(req, e, true, DATA_ERROR);
    }

    @ExceptionHandler(DateTimeUniqueException.class)
    public ResponseEntity<ErrorInfo> conflictError(HttpServletRequest req, DateTimeUniqueException e) {
        return logAndGetErrorInfo(req, e, false, DATA_ERROR, EXCEPTION_DUPLICATE_DATE_TIME);
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
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, e.getMessage());
    }

    @ExceptionHandler({IllegalRequestDataException.class})
    public ResponseEntity<ErrorInfo> invalidRequestDataError(HttpServletRequest req, IllegalRequestDataException e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }
}