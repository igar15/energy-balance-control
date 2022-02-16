package ru.javaprojects.userservice.messaging;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MessageSender {

    public void sendEmailVerifyMessage(String email) {
        //TODO: SEND MESSAGE TO QUEUE TO NOTIFY that email needs to be verified
    }

    public void sendUserDeletedMessage(long id) {
        //TODO: SEND MESSAGE TO QUEUE TO NOTIFY that user has been deleted (EMAIL + USER_ID)
    }

    public void sendDateMessage(LocalDate date, long userId) {
        //TODO: SEND MESSAGE TO QUEUE TO UPDATE BX FOR CURRENT AND NEXT DATES
    }
}