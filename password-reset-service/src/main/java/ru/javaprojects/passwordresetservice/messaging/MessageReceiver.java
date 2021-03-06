package ru.javaprojects.passwordresetservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.javaprojects.energybalancecontrolshared.messaging.UserDeletedMessage;
import ru.javaprojects.passwordresetservice.service.PasswordResetService;

@Component
public class MessageReceiver {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final PasswordResetService service;

    public MessageReceiver(PasswordResetService service) {
        this.service = service;
    }

    @RabbitListener(queues = "${password.reset.queue.name}")
    public void receivePasswordResetMessage(String email) {
        log.info("reset password for email:{}", email);
        try {
            service.sendPasswordResetEmail(email);
        } catch (Exception e) {
            log.info("password reset error: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${passwordResetService.user.deleted.queue.name}")
    public void receiveUserDeletedMessage(UserDeletedMessage userDeletedMessage) {
        log.info("receive {}", userDeletedMessage);
        log.info("delete password reset token for:{}", userDeletedMessage.getEmail());
        try {
            service.delete(userDeletedMessage.getEmail());
        } catch (Exception e) {
            log.info("password reset token deleting error:{}", e.getMessage());
        }
    }
}