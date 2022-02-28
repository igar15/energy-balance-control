package ru.javaprojects.emailverificationservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.javaprojects.emailverificationservice.service.EmailVerificationService;

@Component
public class MessageReceiver {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final EmailVerificationService service;

    public MessageReceiver(EmailVerificationService service) {
        this.service = service;
    }

    @RabbitListener(queues = "emailVerifyQueue")
    public void receiveEmailVerifyMessage(String email) {
        log.info("verify email:{}", email);
        try {
            service.sendVerificationEmail(email);
        } catch (Exception e) {
            log.info("email verification error: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "emailVerificationServiceUserDeletedQueue")
    public void receiveUserDeletedMessage(UserDeletedMessage userDeletedMessage) {
        log.info("receive {}", userDeletedMessage);
        log.info("delete email verification token for user {}", userDeletedMessage.getEmail());
        try {
            service.delete(userDeletedMessage.getEmail());
        } catch (Exception e) {
            log.info("email verification token deleting error: {}", e.getMessage());
        }
    }
}