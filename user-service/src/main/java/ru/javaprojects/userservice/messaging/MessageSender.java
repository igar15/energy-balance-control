package ru.javaprojects.userservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MessageSender {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RabbitTemplate rabbitTemplate;
    private final Exchange exchange;
    private final Environment environment;

    public MessageSender(RabbitTemplate rabbitTemplate, Exchange exchange, Environment environment) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.environment = environment;
    }

    public void sendEmailVerifyMessage(String email) {
        log.info("send email verify message for email={}", email);
        String routingKey = environment.getProperty("email.verify.routingKey");
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, email);
    }

    public void sendPasswordResetMessage(String email) {
        log.info("send password reset message for email={}", email);
        String routingKey = environment.getProperty("password.reset.routingKey");
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, email);
    }

    public void sendDateMessage(LocalDate date, long userId) {
        DateMessage dateMessage = new DateMessage(userId, date, true);
        log.info("send {}", dateMessage);
        String routingKey = environment.getProperty("date.routingKey");
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, dateMessage);
    }

    public void sendUserDeletedMessage(String email, long userId) {
        UserDeletedMessage userDeletedMessage = new UserDeletedMessage(userId, email);
        log.info("send {}", userDeletedMessage);
        String routingKey = environment.getProperty("user.deleted.routingKey");
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, userDeletedMessage);
    }
}