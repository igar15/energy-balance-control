package ru.javaprojects.passwordresetservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.javaprojects.energybalancecontrolshared.messaging.PasswordChangedMessage;

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

    public void sendPasswordChangedMessage(String email, String password) {
        PasswordChangedMessage passwordChangedMessage = new PasswordChangedMessage(email, password);
        log.info("send {}", passwordChangedMessage);
        String routingKey = environment.getProperty("password.changed.routingKey");
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, passwordChangedMessage);
    }
}