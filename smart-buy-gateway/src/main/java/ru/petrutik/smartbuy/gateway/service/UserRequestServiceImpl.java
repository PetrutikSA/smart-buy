package ru.petrutik.smartbuy.gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserRequestServiceImpl implements UserRequestService {
    private final String topicName;
    private final KafkaTemplate<Long, String> kafkaTemplate;

    public UserRequestServiceImpl(@Value("#{@kafkaConfig.getRequestTopicName()}") String topicName,
                                  KafkaTemplate<Long, String> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void registerUser(Long chatId, String firstName) {

    }
}
