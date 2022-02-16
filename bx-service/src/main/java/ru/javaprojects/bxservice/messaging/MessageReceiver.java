package ru.javaprojects.bxservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public void receiveDateMessage() {
        //TODO: RECEIVE MESSAGE FROM QUEUE TO CREATE BX FOR CURRENT DATE
        DateMessage dateMessage = new DateMessage();
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

    public void receiveUserDeletedMessage() {
        //TODO: RECEIVE MESSAGE FROM QUEUE TO DELETE ALL BASIC EXCHANGES FOR DELETED USER
        long userId = 200000;
        log.info("delete all basic exchanges for user {}", userId);
        try {
            service.deleteAll(userId);
        } catch (Exception e) {
            log.info("all basic exchanges deleting error: {}", e.getMessage());
        }
    }
}