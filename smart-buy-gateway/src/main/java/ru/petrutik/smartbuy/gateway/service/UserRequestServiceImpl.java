package ru.petrutik.smartbuy.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.UserRegisterEvent;
import ru.petrutik.smartbuy.gateway.model.Conversation;
import ru.petrutik.smartbuy.gateway.model.ConversationStatus;
import ru.petrutik.smartbuy.gateway.repository.ConversationRepository;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UserRequestServiceImpl implements UserRequestService {
    private final String topicName;
    private final KafkaTemplate<Long, Object> kafkaTemplate;
    private final ConversationRepository conversationRepository;
    private final Logger logger;

    public UserRequestServiceImpl(@Value("#{@kafkaConfig.getRequestTopicName()}") String topicName,
                                  KafkaTemplate<Long, Object> kafkaTemplate,
                                  ConversationRepository conversationRepository) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
        this.conversationRepository = conversationRepository;
        logger = LoggerFactory.getLogger(UserRequestServiceImpl.class);
    }

    @Override
    public void registerUser(Long chatId) {
        Optional<Conversation> conversationOptional = conversationRepository.findById(chatId);
        if (conversationOptional.isEmpty()) {
            conversationRepository.save(new Conversation(chatId, 0, ConversationStatus.NEW, null));
            UserRegisterEvent userRegisterEvent = new UserRegisterEvent(chatId);
            CompletableFuture<SendResult<Long, Object>> future =
                    kafkaTemplate.send(topicName, chatId, userRegisterEvent);
            future.whenComplete(((stringUserRegisterSendResult, throwable) -> {
                if (throwable != null) {
                    logger.error("Failed to send message: {}", throwable.getLocalizedMessage(), throwable);
                } else {
                    logger.info("Message sent successfully {}", stringUserRegisterSendResult);
                }
            }));
        } else {
            Conversation conversation = conversationOptional.get();
            conversation.setStatus(ConversationStatus.NEW);
            conversationRepository.save(conversation);
        }
    }
}
