package ru.javaprojects.trainingservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MessageSender {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final RabbitTemplate rabbitTemplate;
    private final Exchange exchange;

    public MessageSender(RabbitTemplate rabbitTemplate, Exchange exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void sendDateMessage(LocalDate date, long userId) {
        DateMessage dateMessage = new DateMessage(userId, date, false);
        log.info("send {}", dateMessage);
        String routingKey = "date.message";
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, dateMessage);
    }
}