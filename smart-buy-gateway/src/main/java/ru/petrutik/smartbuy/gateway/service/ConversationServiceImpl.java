package ru.petrutik.smartbuy.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.gateway.config.AppConfig;
import ru.petrutik.smartbuy.gateway.model.Conversation;
import ru.petrutik.smartbuy.gateway.model.ConversationStatus;
import ru.petrutik.smartbuy.gateway.repository.ConversationRepository;

import java.util.Optional;

@Service
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final Logger logger;
    private final AppConfig appConfig;

    public ConversationServiceImpl(ConversationRepository conversationRepository, AppConfig appConfig) {
        this.conversationRepository = conversationRepository;
        this.appConfig = appConfig;
        logger = LoggerFactory.getLogger(ConversationServiceImpl.class);
    }

    @Override
    public Conversation getConversationOrRegisterNew(Long chatId) {
        Optional<Conversation> conversationOptional = conversationRepository.findById(chatId);
        if (conversationOptional.isPresent())
            return conversationOptional.get();
        else {
            logger.info("Failed to find conversation with ID: {}, redirect to register conversation", chatId);
            return registerConversation(chatId);
        }
    }

    @Override
    public boolean isRequestLimitReached(Long chatId) {
        return getConversationOrRegisterNew(chatId).getRequestAdded() >= appConfig.getRequestsPerUserLimit();
    }

    @Override
    public ConversationStatus checkConversationStatus(Long chatId) {
        return getConversationOrRegisterNew(chatId).getStatus();
    }

    @Override
    public void makeConversationStatusNew(Long chatId) {
        Conversation conversation = getConversationOrRegisterNew(chatId);
        conversation.setStatus(ConversationStatus.NEW);
        conversationRepository.save(conversation);
    }

    @Override
    public void updateConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }

    private Conversation registerConversation(Long chatId) {
        Conversation conversation = new Conversation(chatId, 0, ConversationStatus.CREATED, null);
        conversation = conversationRepository.save(conversation);
        /*UserRegisterEvent userRegisterEvent = new UserRegisterEvent(chatId);
        sendToKafkaTopic(chatId, userRegisterEvent);*/
        return conversation;
    }
}
