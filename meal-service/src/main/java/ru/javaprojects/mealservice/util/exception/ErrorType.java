package ru.javaprojects.mealservice.util.exception;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    APP_ERROR("Application error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_NOT_FOUND("Data not found", HttpStatus.UNPROCESSABLE_ENTITY),
    DATA_ERROR("Data error", HttpStatus.CONFLICT),
    VALIDATION_ERROR("Validation error", HttpStatus.UNPROCESSABLE_ENTITY),
    UNAUTHORIZED_ERROR("Unauthorized error", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED_ERROR("Access denied error", HttpStatus.FORBIDDEN),
    BAD_TOKEN_ERROR("Bad token error", HttpStatus.UNAUTHORIZED),
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