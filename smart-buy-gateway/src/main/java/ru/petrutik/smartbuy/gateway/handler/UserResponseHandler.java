package ru.petrutik.smartbuy.gateway.handler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${smartbuy.kafka.topic.name.user.response}")
public class UserResponseHandler {
}
