package ru.petrutik.smartbuy.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.AddRequestEvent;
import ru.petrutik.smartbuy.event.ListAllRequestsEvent;
import ru.petrutik.smartbuy.event.RemoveAllRequestsEvent;
import ru.petrutik.smartbuy.event.RemoveRequestEvent;
import ru.petrutik.smartbuy.event.ShowRequestEvent;
import ru.petrutik.smartbuy.event.UserRegisterEvent;
import ru.petrutik.smartbuy.gateway.config.AppConfig;
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
    private final AppConfig appConfig;

    public UserRequestServiceImpl(@Value("#{@kafkaConfig.getRequestTopicName()}") String topicName,
                                  KafkaTemplate<Long, Object> kafkaTemplate,
                                  ConversationRepository conversationRepository,
                                  AppConfig appConfig) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
        this.conversationRepository = conversationRepository;
        this.appConfig = appConfig;
        logger = LoggerFactory.getLogger(UserRequestServiceImpl.class);
    }

    @Override
    public void registerUserOrSetStatusToNew(Long chatId) {
        Conversation conversation = getConversationOrRegisterNew(chatId);
        if (conversation.getStatus() != ConversationStatus.NEW) {
            conversation.setStatus(ConversationStatus.NEW);
            conversationRepository.save(conversation);
        }
    }

    @Override
    public ConversationStatus checkConversationStatus(Long chatId) {
        return getConversationOrRegisterNew(chatId).getStatus();
    }

    @Override
    public boolean isRequestLimitReached(Long chatId) {
        return getConversationOrRegisterNew(chatId).getRequestAdded() >= appConfig.getRequestsPerUserLimit();
    }

    private Conversation registerConversation(Long chatId) {
        Conversation conversation = new Conversation(chatId, 0, ConversationStatus.NEW, null);
        conversation = conversationRepository.save(conversation);
        UserRegisterEvent userRegisterEvent = new UserRegisterEvent(chatId);
        sendToKafkaTopic(chatId, userRegisterEvent);
        return conversation;
    }

    private Conversation getConversationOrRegisterNew(Long chatId) {
        Optional<Conversation> conversationOptional = conversationRepository.findById(chatId);
        if (conversationOptional.isPresent())
            return conversationOptional.get();
        else {
            logger.info("Failed to find conversation with ID: {}, redirect to register conversation", chatId);
            return registerConversation(chatId);
        }
    }

    @Override
    public void addRequest(Long chatId, ConversationStatus conversationStatus, String clientMessage) {
        Conversation conversation = getConversationOrRegisterNew(chatId);
        switch (conversationStatus){
            case ADD0 -> {
                conversation.setStatus(ConversationStatus.ADD1);
                conversationRepository.save(conversation);
            }
            case ADD1 -> {
                conversation.setClientInput(clientMessage);
                conversation.setStatus(ConversationStatus.ADD2);
                conversationRepository.save(conversation);
            }
            case ADD2 -> {
                Integer price;
                try {
                    price = Integer.parseInt(clientMessage);
                } catch (NumberFormatException e) {
                    logger.error("Error when parsing price: {}", clientMessage, e);
                    price = null;
                }
                AddRequestEvent addRequestEvent = new AddRequestEvent(chatId, conversation.getClientInput(), price);
                sendToKafkaTopic(chatId, addRequestEvent);
                conversation.setClientInput(null);
                conversation.setRequestAdded(conversation.getRequestAdded() + 1);
                conversation.setStatus(ConversationStatus.NEW);
            }
        }
    }

    @Override
    public void listOfAllRequests(Long chatId, ConversationStatus conversationStatus) {
        Conversation conversation = getConversationOrRegisterNew(chatId);
        ListAllRequestsEvent listAllRequestsEvent = new ListAllRequestsEvent(chatId);
        sendToKafkaTopic(chatId, listAllRequestsEvent);
        conversation.setStatus(conversationStatus);
        conversationRepository.save(conversation);
    }

    @Override
    public void showRequest(Long chatId, Integer requestNumber) {
        Conversation conversation = getConversationOrRegisterNew(chatId);
        ShowRequestEvent showRequestEvent = new ShowRequestEvent(chatId, requestNumber);
        sendToKafkaTopic(chatId, showRequestEvent);
        conversation.setStatus(ConversationStatus.SHOW2);
        conversationRepository.save(conversation);
    }

    @Override
    public void removeRequest(Long chatId, Integer requestNumber) {
        Conversation conversation = getConversationOrRegisterNew(chatId);
        RemoveRequestEvent removeRequestEvent = new RemoveRequestEvent(chatId, requestNumber);
        sendToKafkaTopic(chatId, removeRequestEvent);
        conversation.setStatus(ConversationStatus.DELETE2);
        conversationRepository.save(conversation);
    }

    @Override
    public void removeAll(Long chatId, ConversationStatus conversationStatus) {
        Conversation conversation = getConversationOrRegisterNew(chatId);
        switch (conversationStatus) {
            case DELETE_ALL0 -> {
                conversation.setStatus(ConversationStatus.DELETE_ALL1);
                conversationRepository.save(conversation);
            }
            case DELETE_ALL1 -> {
                RemoveAllRequestsEvent removeAllRequestsEvent = new RemoveAllRequestsEvent(chatId);
                sendToKafkaTopic(chatId, removeAllRequestsEvent);
                conversation.setStatus(ConversationStatus.DELETE_ALL2);
                conversationRepository.save(conversation);
            }
        }
    }

    @Override
    public void makeConversationStatusNew(Long chatId) {
        Conversation conversation = getConversationOrRegisterNew(chatId);
        conversation.setStatus(ConversationStatus.NEW);
        conversationRepository.save(conversation);
    }

    private void sendToKafkaTopic(Long key, Object value) {
        CompletableFuture<SendResult<Long, Object>> future =
                kafkaTemplate.send(topicName, key, value);
        future.whenComplete(((stringSendResult, throwable) -> {
            if (throwable != null) {
                logger.error("Failed to send message: {}", throwable.getLocalizedMessage(), throwable);
            } else {
                logger.info("Message sent successfully {}", stringSendResult);
            }
        }));
    }
}
