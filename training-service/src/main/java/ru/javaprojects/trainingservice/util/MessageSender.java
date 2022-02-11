package ru.javaprojects.trainingservice.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MessageSender {

    public void sendMessageDateCreated(LocalDate date, long userId) {
        //TODO: SEND MESSAGE TO QUEUE TO CREATE BX FOR CURRENT DATE
    }
}