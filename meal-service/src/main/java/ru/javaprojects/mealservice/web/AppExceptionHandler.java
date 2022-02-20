package ru.javaprojects.mealservice.web;

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

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.*;


@RestControllerAdvice
public class AppExceptionHandler extends BasicAppExceptionHandler {
    public static final String EXCEPTION_DUPLICATE_DATE_TIME = "Meal with this date and time already exists";
    public static final String MEAL_DATE_TIME_CONSTRAIN = "meals_unique_user_datetime_idx";


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> notFoundError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorInfo> conflictError(HttpServletRequest req, DataIntegrityViolationException e) {
        String rootMsg = ValidationUtil.getRootCause(e).getMessage();
        if (rootMsg != null) {
            String lowerCaseMsg = rootMsg.toLowerCase();
            if (lowerCaseMsg.contains(MEAL_DATE_TIME_CONSTRAIN)) {
                return logAndGetErrorInfo(req, e, false, DATA_ERROR, EXCEPTION_DUPLICATE_DATE_TIME);
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
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR, e.getMessage());
    }

    @ExceptionHandler({IllegalRequestDataException.class})
    public ResponseEntity<ErrorInfo> invalidRequestDataError(HttpServletRequest req, IllegalRequestDataException e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }
}