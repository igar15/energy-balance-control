package ru.javaprojects.trainingservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.javaprojects.energybalancecontrolshared.messaging.UserDeletedMessage;
import ru.javaprojects.trainingservice.service.ExerciseTypeService;

@Component
public class MessageReceiver {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ExerciseTypeService service;

    public MessageReceiver(ExerciseTypeService service) {
        this.service = service;
    }

    @RabbitListener(queues = "${trainingService.user.deleted.queue.name}")
    public void receiveUserDeletedMessage(UserDeletedMessage userDeletedMessage) {
        log.info("receive {}", userDeletedMessage);
        log.info("delete all training data for user {}", userDeletedMessage.getUserId());
        try {
            service.deleteAll(userDeletedMessage.getUserId());
        } catch (Exception e) {
            log.info("exercise types deleting error: {}", e.getMessage());
        }
    }
}