package ru.petrutik.smartbuy.gateway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.petrutik.smartbuy.event.response.AddResponseEvent;
import ru.petrutik.smartbuy.event.response.ListAllResponseEvent;

import ru.petrutik.smartbuy.event.response.RemoveResponseEvent;
import ru.petrutik.smartbuy.event.response.ShowResponseEvent;
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
        logEventReceiving(addResponseEvent.getClass().getName(), addResponseEvent.getChatId());
        userResponseService.updateRequestCount(addResponseEvent.getChatId(), addResponseEvent.getRemainRequestsCount());
    }

    @KafkaHandler
    public void handleListAllResponseEvent(ListAllResponseEvent listAllResponseEvent) {
        logEventReceiving(listAllResponseEvent.getClass().getName(), listAllResponseEvent.getChatId());
        userResponseService.listAllResponse(listAllResponseEvent.getChatId(), listAllResponseEvent.getRequests());
    }

    @KafkaHandler
    public void handleShowResponseEvent(ShowResponseEvent showResponseEvent) {
        logEventReceiving(showResponseEvent.getClass().getName(), showResponseEvent.getChatId());
        userResponseService.showResponse(showResponseEvent.getChatId(), showResponseEvent.getRequestQuery(),
                showResponseEvent.getProducts());
    }

    @KafkaHandler
    public void handleRemoveResponseEvent(RemoveResponseEvent removeResponseEvent) {
        logEventReceiving(removeResponseEvent.getClass().getName(), removeResponseEvent.getChatId());
        userResponseService.removeResponse(removeResponseEvent.getChatId(), removeResponseEvent.getRequestNumber(),
                removeResponseEvent.getRemainRequestsCount());
    }

    private void logEventReceiving(String eventClassName, Long chatId) {
        logger.info("Received {}, chat id = {}", eventClassName, chatId);
    }
}
