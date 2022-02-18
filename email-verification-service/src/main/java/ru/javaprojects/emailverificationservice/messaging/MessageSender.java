package ru.javaprojects.emailverificationservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void sendEmailVerifiedMessage(String email) {
        log.info("send email verified message for {}", email);
        //TODO: SEND MESSAGE TO QUEUE TO NOTIFY that email has been verified
    }
}