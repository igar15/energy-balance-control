package ru.javaprojects.userservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.javaprojects.userservice.service.UserService;

@Component
public class MessageReceiver {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserService service;

    public MessageReceiver(UserService service) {
        this.service = service;
    }

    public void receiveEmailVerifiedMessage() {
        //TODO: RECEIVE MESSAGE FROM QUEUE TO ENABLE USER
        String email = "";
        log.info("email has been verified, enable user:{}", email);
        try {
            service.enable(email);
        } catch (Exception e) {
            log.info("user enabling error: {}", e.getMessage());
        }
    }

    public void receivePasswordChangedMessage() {
        //TODO: RECEIVE MESSAGE FROM QUEUE TO CHANGE PASSWORD
        String email = "";
        String password = "";
        log.info("change password for user:{}", email);
        try {
            service.changePassword(email, password);
        } catch (Exception e) {
            log.info("user change password error: {}", e.getMessage());
        }
    }
}