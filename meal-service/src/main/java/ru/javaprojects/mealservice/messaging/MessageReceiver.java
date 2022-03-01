package ru.javaprojects.mealservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.javaprojects.mealservice.service.MealService;

@Component
public class MessageReceiver {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MealService service;

    public MessageReceiver(MealService service) {
        this.service = service;
    }

    @RabbitListener(queues = "${mealService.user.deleted.queue.name}")
    public void receiveUserDeletedMessage(UserDeletedMessage userDeletedMessage) {
        log.info("receive {}", userDeletedMessage);
        log.info("delete all meals for user {}", userDeletedMessage.getUserId());
        try {
            service.deleteAll(userDeletedMessage.getUserId());
        } catch (Exception e) {
            log.info("meals deleting error: {}", e.getMessage());
        }
    }
}