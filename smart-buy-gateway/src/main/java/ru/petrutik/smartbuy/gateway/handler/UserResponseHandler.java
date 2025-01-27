package ru.petrutik.smartbuy.gateway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.petrutik.smartbuy.event.response.AddResponseEvent;
import ru.petrutik.smartbuy.event.response.ListAllResponseEvent;

import ru.petrutik.smartbuy.gateway.service.UserResponseService;
import ru.petrutik.smartbuy.gateway.service.UserResponseServiceImpl;

@Component
@KafkaListener(topics = "#{@kafkaConfig.getResponseTopicName()}")
public class UserResponseHandler {
    private final UserResponseService userResponseService;
    private final Logger logger;

    public UserResponseHandler(UserResponseService userResponseService) {
        this.userResponseService = userResponseService;
        logger = LoggerFactory.getLogger(UserResponseServiceImpl.class);
    }

    @KafkaHandler
    public void handleAddResponseEvent(AddResponseEvent addResponseEvent) {
        logger.info("Received add response event, chat id = {}", addResponseEvent.getChatId());
        userResponseService.updateRequestCount(addResponseEvent.getChatId(), addResponseEvent.getRemainRequestsCount());
    }

    @KafkaHandler
    public void handleListAllResponseEvent(ListAllResponseEvent listAllResponseEvent) {
        logger.info("Received list all response event, chat id = {}", listAllResponseEvent.getChatId());
        userResponseService.listAllResponse(listAllResponseEvent.getChatId(), listAllResponseEvent.getRequests());
    }
}
