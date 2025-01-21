package ru.petrutik.smartbuy.requestservice.handler;

import org.springframework.kafka.annotation.KafkaListener;

@KafkaListener(topics = "#{@kafkaConfig.getRequestTopicName()}")
public class UserRequestHandler {
}
