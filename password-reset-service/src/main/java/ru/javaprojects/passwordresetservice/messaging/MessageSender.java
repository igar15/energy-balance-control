package ru.javaprojects.passwordresetservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RabbitTemplate rabbitTemplate;
    private final Exchange exchange;

    public MessageSender(RabbitTemplate rabbitTemplate, Exchange exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void sendPasswordChangedMessage(String email, String password) {
        PasswordChangedMessage passwordChangedMessage = new PasswordChangedMessage(email, password);
        log.info("send {}", passwordChangedMessage);
        String routingKey = "password.changed.message";
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, passwordChangedMessage);
    }
}