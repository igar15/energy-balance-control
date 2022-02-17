package ru.javaprojects.userservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MessageSender {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void sendEmailVerifyMessage(String email) {
        log.info("send email verify message for email={}", email);
        //TODO: SEND MESSAGE TO QUEUE TO NOTIFY that email needs to be verified
    }

    public void sendUserDeletedMessage(String email, long userId) {
        log.info("send user deleted message for user={}, email={}", userId, email);
        //TODO: SEND MESSAGE TO QUEUE TO NOTIFY that user has been deleted (EMAIL + USER_ID)
    }

    public void sendDateMessage(LocalDate date, long userId) {
        log.info("send date message for user={}, date={}", userId, date);
        //TODO: SEND MESSAGE TO QUEUE TO UPDATE BX FOR CURRENT AND NEXT DATES
    }

    public void sendPasswordResetMessage(String email) {
        log.info("send password reset message for email={}", email);
        //TODO: SEND MESSAGE TO QUEUE TO RESET PASSWORD FOR USER
    }
}