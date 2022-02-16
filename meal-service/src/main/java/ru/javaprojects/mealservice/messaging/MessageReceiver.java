package ru.javaprojects.mealservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.javaprojects.mealservice.service.MealService;

@Component
public class MessageReceiver {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MealService service;

    public MessageReceiver(MealService service) {
        this.service = service;
    }

    public void receiveUserDeletedMessage() {
        //TODO: RECEIVE MESSAGE FROM QUEUE TO DELETE ALL MEALS FOR DELETED USER
        long userId = 200000;
        log.info("delete all meals for user {}", userId);
        try {
            service.deleteAll(userId);
        } catch (Exception e) {
            log.info("all meals deleting error: {}", e.getMessage());
        }
    }
}