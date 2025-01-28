package ru.petrutik.smartbuy.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.user.request.AddRequestEvent;
import ru.petrutik.smartbuy.event.user.request.ListAllRequestsEvent;
import ru.petrutik.smartbuy.event.user.request.RemoveAllRequestsEvent;
import ru.petrutik.smartbuy.event.user.request.RemoveRequestEvent;
import ru.petrutik.smartbuy.event.user.request.ShowRequestEvent;
import ru.petrutik.smartbuy.event.user.request.UserRegisterEvent;
import ru.petrutik.smartbuy.gateway.model.Conversation;
import ru.petrutik.smartbuy.gateway.model.ConversationStatus;

import java.util.concurrent.CompletableFuture;

@Service
public class UserRequestServiceImpl implements UserRequestService {
    private final String topicName;
    private final KafkaTemplate<Long, Object> kafkaTemplate;
    private final ConversationService conversationService;
    private final Logger logger;

    public UserRequestServiceImpl(@Value("#{@kafkaConfig.getRequestTopicName()}") String topicName,
                                  KafkaTemplate<Long, Object> kafkaTemplate,
                                  ConversationService conversationService) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
        this.conversationService = conversationService;
        logger = LoggerFactory.getLogger(UserRequestServiceImpl.class);
    }

    @Override
    public void registerUserOrSetStatusToNew(Long chatId) {
        Conversation conversation = conversationService.getConversationOrRegisterNew(chatId);
        if (conversation.getStatus() != ConversationStatus.NEW) {
            if (conversation.getStatus() == ConversationStatus.CREATED) {
                UserRegisterEvent userRegisterEvent = new UserRegisterEvent(chatId);
                sendToKafkaTopic(chatId, userRegisterEvent);
            }
            conversationService.makeConversationStatusNew(chatId);
        }
    }

    @Override
    public void addRequest(Long chatId, ConversationStatus conversationStatus, String clientMessage) {
        Conversation conversation = conversationService.getConversationOrRegisterNew(chatId);
        switch (conversationStatus){
            case ADD0 -> {
                conversation.setStatus(ConversationStatus.ADD1);
                conversationService.updateConversation(conversation);
            }
            case ADD1 -> {
                conversation.setClientInput(clientMessage);
                conversation.setStatus(ConversationStatus.ADD2);
                conversationService.updateConversation(conversation);
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
                conversationService.updateConversation(conversation);
            }
        }
    }

    @Override
    public void listOfAllRequests(Long chatId, ConversationStatus conversationStatus) {
        Conversation conversation = conversationService.getConversationOrRegisterNew(chatId);
        ListAllRequestsEvent listAllRequestsEvent = new ListAllRequestsEvent(chatId);
        sendToKafkaTopic(chatId, listAllRequestsEvent);
        conversation.setStatus(conversationStatus);
        conversationService.updateConversation(conversation);
    }

    @Override
    public void showRequest(Long chatId, Integer requestNumber) {
        Conversation conversation = conversationService.getConversationOrRegisterNew(chatId);
        ShowRequestEvent showRequestEvent = new ShowRequestEvent(chatId, requestNumber);
        sendToKafkaTopic(chatId, showRequestEvent);
        conversation.setStatus(ConversationStatus.SHOW2);
        conversationService.updateConversation(conversation);
    }

    @Override
    public void removeRequest(Long chatId, Integer requestNumber) {
        Conversation conversation = conversationService.getConversationOrRegisterNew(chatId);
        RemoveRequestEvent removeRequestEvent = new RemoveRequestEvent(chatId, requestNumber);
        sendToKafkaTopic(chatId, removeRequestEvent);
        conversation.setStatus(ConversationStatus.DELETE2);
        conversationService.updateConversation(conversation);
    }

    @Override
    public void removeAll(Long chatId, ConversationStatus conversationStatus) {
        Conversation conversation = conversationService.getConversationOrRegisterNew(chatId);
        switch (conversationStatus) {
            case DELETE_ALL0 -> {
                conversation.setStatus(ConversationStatus.DELETE_ALL1);
                conversationService.updateConversation(conversation);
            }
            case DELETE_ALL1 -> {
                RemoveAllRequestsEvent removeAllRequestsEvent = new RemoveAllRequestsEvent(chatId);
                sendToKafkaTopic(chatId, removeAllRequestsEvent);
                conversation.setStatus(ConversationStatus.DELETE_ALL2);
                conversationService.updateConversation(conversation);
            }
        }
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
