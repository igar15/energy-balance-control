package ru.javaprojects.emailverificationservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.javaprojects.emailverificationservice.service.EmailVerificationService;
import ru.javaprojects.emailverificationservice.util.exception.EmailVerificationException;

@Component
public class MessageReceiver {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final EmailVerificationService service;

    public MessageReceiver(EmailVerificationService service) {
        this.service = service;
    }

    public void receiveEmailVerifyMessage() {
        //TODO: RECEIVE MESSAGE FROM QUEUE TO VERIFY USER EMAIL
        String email = "";
        log.info("verify email:{}", email);
        try {
            service.sendVerificationEmail(email);
        } catch (EmailVerificationException e) {
            log.info("email verification error: {}", e.getMessage());
        }
    }

    public void receiveUserDeletedMessage() {
        //TODO: RECEIVE MESSAGE FROM QUEUE TO DELETE VERIFICATION EMAIL TOKEN FOR DELETED USER
        String email = "user1@test.com";
        log.info("delete email verification token:{}", email);
        try {
            service.delete(email);
        } catch (Exception e) {
            log.info("email verification token deleting error: {}", e.getMessage());
        }
    }
}