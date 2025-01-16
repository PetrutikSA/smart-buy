package ru.petrutik.smartbuy.gateway.handler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "#{@kafkaConfig.getResponseTopicName()}")
public class UserResponseHandler {
}
