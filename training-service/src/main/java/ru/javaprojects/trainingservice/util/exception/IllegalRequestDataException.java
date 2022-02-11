package ru.javaprojects.trainingservice.util.exception;

public class IllegalRequestDataException extends RuntimeException{
    public IllegalRequestDataException(String message) {
        super(message);
    }
}