package ru.javaprojects.passwordresetservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.javaprojects.passwordresetservice.service.PasswordResetService;

@Component
public class MessageReceiver {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final PasswordResetService service;

    public MessageReceiver(PasswordResetService service) {
        this.service = service;
    }

    public void receivePasswordResetMessage() {
        //TODO: RECEIVE MESSAGE FROM QUEUE TO RESET USER PASSWORD
        String email = "";
        log.info("reset password for email:{}", email);
        try {
            service.sendPasswordResetEmail(email);
        } catch (Exception e) {
            log.info("password reset error: {}", e.getMessage());
        }
    }

    public void receiveUserDeletedMessage() {
        //TODO: RECEIVE MESSAGE FROM QUEUE TO DELETE PASSWORD RESET TOKEN FOR DELETED USER
        String email = "user1@test.com";
        log.info("delete password reset token:{}", email);
        try {
            service.delete(email);
        } catch (Exception e) {
            log.info("password reset token deleting error: {}", e.getMessage());
        }
    }
}