package ru.javaprojects.userservice.util.exception;

public class EmailVerificationException extends RuntimeException {
    public EmailVerificationException(String message) {
        super(message);
    }
}