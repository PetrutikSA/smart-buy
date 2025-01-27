package ru.petrutik.smartbuy.gateway.service;

import ru.petrutik.smartbuy.gateway.model.Conversation;
import ru.petrutik.smartbuy.gateway.model.ConversationStatus;

public interface ConversationService {
    Conversation getConversationOrRegisterNew(Long chatId);

    boolean isRequestLimitReached(Long chatId);

    ConversationStatus checkConversationStatus(Long chatId);

    void makeConversationStatusNew(Long chatId);

    void updateConversation(Conversation conversation);
}
