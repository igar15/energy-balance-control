package ru.javaprojects.emailverificationservice.messaging;

import org.springframework.stereotype.Component;

@Component
public class MessageSender {

    public void sendEmailVerifiedMessage(String email) {
        //TODO: SEND MESSAGE TO QUEUE TO NOTIFY that email has been verified
    }
}