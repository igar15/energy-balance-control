package ru.javaprojects.passwordresetservice.util.exception;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    APP_ERROR("Application error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_NOT_FOUND("Data not found", HttpStatus.UNPROCESSABLE_ENTITY),
    VALIDATION_ERROR("Validation error", HttpStatus.UNPROCESSABLE_ENTITY),
    WRONG_REQUEST("Wrong request", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final HttpStatus status;

    ErrorType(String errorCode, HttpStatus status) {
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}