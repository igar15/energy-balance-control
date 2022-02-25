package ru.javaprojects.energybalanceservice.web;

import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.javaprojects.energybalancecontrolshared.util.exception.ErrorInfo;
import ru.javaprojects.energybalancecontrolshared.web.BasicAppExceptionHandler;

import javax.servlet.http.HttpServletRequest;

import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.FEIGN_ERROR;


@RestControllerAdvice
public class AppExceptionHandler extends BasicAppExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorInfo> feignError(HttpServletRequest req, FeignException e) {
        return logAndGetErrorInfo(req, e, false, FEIGN_ERROR);
    }
}