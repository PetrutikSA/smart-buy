package ru.petrutik.smartbuy.requestservice.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.petrutik.smartbuy.event.UserRegisterEvent;
import ru.petrutik.smartbuy.requestservice.service.UserService;

@Component
@KafkaListener(topics = "#{@kafkaConfig.getRequestTopicName()}")
public class UserRequestHandler {
    private final UserService userService;

    public UserRequestHandler(UserService userService) {
        this.userService = userService;
    }

    @KafkaHandler
    public void handleUserRegisterEvent(UserRegisterEvent userRegisterEvent) {
        userService.registerUser(userRegisterEvent.getChatId());
    }
}
