package ru.javaprojects.trainingservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.javaprojects.trainingservice.service.ExerciseTypeService;

@Component
public class MessageReceiver {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ExerciseTypeService service;

    public MessageReceiver(ExerciseTypeService service) {
        this.service = service;
    }

    public void receiveUserDeletedMessage() {
        //TODO: RECEIVE MESSAGE FROM QUEUE TO DELETE ALL EXERCISE TYPES AND EXERCISES FOR DELETED USER
        long userId = 200000;
        log.info("delete all exercise types and exercises for user {}", userId);
        try {
            service.deleteAll(userId);
        } catch (Exception e) {
            log.info("all exercise types and exercises deleting error: {}", e.getMessage());
        }
    }
}