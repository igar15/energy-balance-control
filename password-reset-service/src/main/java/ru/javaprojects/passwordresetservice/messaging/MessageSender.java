package ru.javaprojects.passwordresetservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void sendChangePasswordMessage(String email, String password) {
        log.info("send change password message for {}", email);
        //TODO: SEND MESSAGE TO QUEUE TO NOTIFY that password has been changed
    }
}