package ru.petrutik.smartbuy.requestservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.petrutik.smartbuy.event.user.request.AddRequestEvent;
import ru.petrutik.smartbuy.event.user.request.ListAllRequestsEvent;
import ru.petrutik.smartbuy.event.user.request.RemoveAllRequestsEvent;
import ru.petrutik.smartbuy.event.user.request.RemoveRequestEvent;
import ru.petrutik.smartbuy.event.user.request.ShowRequestEvent;
import ru.petrutik.smartbuy.event.user.request.UserRegisterEvent;
import ru.petrutik.smartbuy.requestservice.service.RequestService;
import ru.petrutik.smartbuy.requestservice.service.UserService;

@Component
@KafkaListener(topics = "#{@kafkaConfig.getUserRequestTopicName()}")
public class UserRequestHandler {
    private final UserService userService;
    private final RequestService requestService;
    private final Logger logger;

    public UserRequestHandler(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
        this.logger = LoggerFactory.getLogger(UserRequestHandler.class);
    }

    @KafkaHandler
    public void handleUserRegisterEvent(UserRegisterEvent userRegisterEvent) {
        logger.info("Received user register event, chat id = {}", userRegisterEvent.getChatId());
        userService.registerUser(userRegisterEvent.getChatId());
    }

    @KafkaHandler
    public void handleAddRequestEvent(AddRequestEvent addRequestEvent) {
        logger.info("Received add request event, chat id = {}, and search query = {}",
                addRequestEvent.getChatId(), addRequestEvent.getSearchQuery());
        requestService.addRequest(addRequestEvent.getChatId(), addRequestEvent.getSearchQuery(),
                addRequestEvent.getMaxPrice());
    }

    @KafkaHandler
    public void handleListAllRequestsEvent(ListAllRequestsEvent listAllRequestsEvent) {
        logger.info("Received list all request event, chat id = {}", listAllRequestsEvent.getChatId());
        requestService.getAllRequests(listAllRequestsEvent.getChatId());
    }

    @KafkaHandler
    public void handleShowRequestEvent(ShowRequestEvent showRequestEvent) {
        logger.info("Received show request event, chat id = {}", showRequestEvent.getChatId());
        requestService.showRequest(showRequestEvent.getChatId(), showRequestEvent.getRequestNumber());
    }

    @KafkaHandler
    public void handleRemoveRequestEvent(RemoveRequestEvent removeRequestEvent) {
        logger.info("Received remove request event, chat id = {}", removeRequestEvent.getChatId());
        requestService.removeRequest(removeRequestEvent.getChatId(), removeRequestEvent.getRequestNumber());
    }

    @KafkaHandler
    public void handleRemoveAllRequestsEvent(RemoveAllRequestsEvent removeAllRequestsEvent) {
        logger.info("Received remove all requests event, chat id = {}", removeAllRequestsEvent.getChatId());
        requestService.removeAllRequest(removeAllRequestsEvent.getChatId());
    }
}
