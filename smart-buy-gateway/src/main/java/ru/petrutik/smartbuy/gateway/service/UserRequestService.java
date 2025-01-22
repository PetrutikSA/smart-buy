package ru.petrutik.smartbuy.gateway.service;

import ru.petrutik.smartbuy.gateway.model.ConversationStatus;

public interface UserRequestService {
    void registerUserOrSetStatusToNew(Long chatId);

    ConversationStatus checkConversationStatus(Long chatId);

    boolean isRequestLimitReached(Long chatId);

    void addRequest(Long chatId, ConversationStatus conversationStatus, String clientMessage);
}
