package ru.javaprojects.userservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.javaprojects.userservice.service.UserService;

@Component
public class MessageReceiver {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserService service;

    public MessageReceiver(UserService service) {
        this.service = service;
    }

    @RabbitListener(queues = "${email.confirmed.queue.name}")
    public void receiveEmailConfirmedMessage(String email) {
        log.info("email has been confirmed, enable user:{}", email);
        try {
            service.enable(email);
        } catch (Exception e) {
            log.info("user enabling error: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${password.changed.queue.name}")
    public void receivePasswordChangedMessage(PasswordChangedMessage passwordChangedMessage) {
        log.info("change password for user:{}", passwordChangedMessage.getEmail());
        try {
            service.changePassword(passwordChangedMessage.getEmail(), passwordChangedMessage.getPassword());
        } catch (Exception e) {
            log.info("change password error: {}", e.getMessage());
        }
    }
}