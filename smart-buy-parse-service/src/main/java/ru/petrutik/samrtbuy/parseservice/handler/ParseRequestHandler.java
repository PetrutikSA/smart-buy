package ru.petrutik.samrtbuy.parseservice.handler;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "#{@kafkaConfig.getRequestTopicName()}")
public class ParseRequestHandler {
}
