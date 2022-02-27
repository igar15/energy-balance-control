package ru.javaprojects.bxservice.util.exception;

public class UserServiceConnectionException extends RuntimeException {
    public UserServiceConnectionException(String message) {
        super(message);
    }
}