package ru.javaprojects.emailverificationservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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

    public void sendEmailConfirmedMessage(String email) {
        log.info("send email confirmed message for {}", email);
        String routingKey = environment.getProperty("email.confirmed.routingKey");
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, email);
    }
}