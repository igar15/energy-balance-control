package ru.javaprojects.bxservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import ru.javaprojects.bxservice.service.BasicExchangeService;

@Component
public class MessageReceiver {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final BasicExchangeService service;

    public MessageReceiver(BasicExchangeService service) {
        this.service = service;
    }

    @RabbitListener(queues = "${date.queue.name}")
    public void receiveDateMessage(DateMessage dateMessage) {
        log.info("receive {}", dateMessage);
        if (dateMessage.isUserBxDetailsChanged()) {
            log.info("update basic exchanges for {} from date {}", dateMessage.getUserId(), dateMessage.getDate());
            service.updateBasicExchanges(dateMessage.getDate(), dateMessage.getUserId());
        } else {
            log.info("create basic exchange for {} for date {}", dateMessage.getUserId(), dateMessage.getDate());
            try {
                service.create(dateMessage.getDate(), dateMessage.getUserId());
            } catch (DataAccessException e) {
                log.info("basic exchange for {} for date {} already exists", dateMessage.getUserId(), dateMessage.getDate());
            }
        }
    }

    @RabbitListener(queues = "${bxService.user.deleted.queue.name}")
    public void receiveUserDeletedMessage(UserDeletedMessage userDeletedMessage) {
        log.info("receive {}", userDeletedMessage);
        log.info("delete all basic exchanges for user {}", userDeletedMessage.getUserId());
        try {
            service.deleteAll(userDeletedMessage.getUserId());
        } catch (Exception e) {
            log.info("basic exchanges deleting error: {}", e.getMessage());
        }
    }
}