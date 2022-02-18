package ru.javaprojects.mealservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MessageSender {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public void sendDateCreatedMessage(LocalDate date, long userId) {
        log.info("send date message for user={}, date={}", userId, date);
        //TODO: SEND MESSAGE TO QUEUE TO CREATE BX FOR CURRENT DATE
    }
}