package ru.petrutik.smartbuy.gateway.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.petrutik.smartbuy.event.user.response.AddResponseEvent;
import ru.petrutik.smartbuy.event.user.response.ExceptionResponseEvent;
import ru.petrutik.smartbuy.event.user.response.ListAllResponseEvent;

import ru.petrutik.smartbuy.event.user.response.RemoveAllResponseEvent;
import ru.petrutik.smartbuy.event.user.response.RemoveResponseEvent;
import ru.petrutik.smartbuy.event.user.response.ShowResponseEvent;
import ru.petrutik.smartbuy.event.user.response.ShowResultsAfterAddResponseEvent;
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

    @KafkaHandler
    public void handleRemoveAllResponseEvent(RemoveAllResponseEvent removeAllResponseEvent) {
        logEventReceiving(removeAllResponseEvent.getClass().getName(), removeAllResponseEvent.getChatId());
        userResponseService.removeAllResponse(removeAllResponseEvent.getChatId());
    }

    @KafkaHandler
    public void handleExceptionResponseEvent(ExceptionResponseEvent exceptionResponseEvent) {
        logEventReceiving(exceptionResponseEvent.getClass().getName(), exceptionResponseEvent.getChatId());
        userResponseService.exceptionResponse(exceptionResponseEvent.getChatId(), exceptionResponseEvent.getMessage());
    }

    @KafkaHandler
    public void handleShowResultsAfterAddResponseEvent(ShowResultsAfterAddResponseEvent showResultsAfterAddResponseEvent) {
        logEventReceiving(showResultsAfterAddResponseEvent.getClass().getName(),
                showResultsAfterAddResponseEvent.getChatId());
        userResponseService.showResultsAfterAddNewRequest(showResultsAfterAddResponseEvent.getChatId(),
                showResultsAfterAddResponseEvent.getRequestQuery(), showResultsAfterAddResponseEvent.getProducts());
    }

    private void logEventReceiving(String eventClassName, Long chatId) {
        logger.info("Received {}, chat id = {}", eventClassName, chatId);
    }
}
