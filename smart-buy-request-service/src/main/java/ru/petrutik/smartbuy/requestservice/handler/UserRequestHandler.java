package ru.petrutik.smartbuy.requestservice.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.petrutik.smartbuy.event.request.AddRequestEvent;
import ru.petrutik.smartbuy.event.request.ListAllRequestsEvent;
import ru.petrutik.smartbuy.event.request.UserRegisterEvent;
import ru.petrutik.smartbuy.requestservice.service.RequestService;
import ru.petrutik.smartbuy.requestservice.service.UserService;

@Component
@KafkaListener(topics = "#{@kafkaConfig.getRequestTopicName()}")
public class UserRequestHandler {
    private final UserService userService;
    private final RequestService requestService;

    public UserRequestHandler(UserService userService, RequestService requestService) {
        this.userService = userService;
        this.requestService = requestService;
    }

    @KafkaHandler
    public void handleUserRegisterEvent(UserRegisterEvent userRegisterEvent) {
        userService.registerUser(userRegisterEvent.getChatId());
    }

    @KafkaHandler
    public void handleAddRequestEvent(AddRequestEvent addRequestEvent) {
        requestService.addRequest(addRequestEvent.getChatId(), addRequestEvent.getSearchQuery(), addRequestEvent.getMaxPrice());
    }

    @KafkaHandler
    public void handleListAllRequestsEvent(ListAllRequestsEvent listAllRequestsEvent) {
        requestService.getAllRequests(listAllRequestsEvent.getChatId());
    }

}
