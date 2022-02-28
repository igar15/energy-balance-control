package ru.javaprojects.mealservice.messaging;

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

    public void sendDateCreatedMessage(LocalDate date, long userId) {
        log.info("send date message for user={}, date={}", userId, date);
        //TODO: SEND MESSAGE TO QUEUE TO CREATE BX FOR CURRENT DATE
        DateMessage dateMessage = new DateMessage(userId, date, false);
        String routingKey = "date.created";
        String message = "Date created for user:" + userId + " date:" + date;
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, dateMessage);
    }
}