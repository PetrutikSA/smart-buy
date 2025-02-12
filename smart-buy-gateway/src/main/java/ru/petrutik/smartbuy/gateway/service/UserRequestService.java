package ru.petrutik.smartbuy.gateway.service;

import ru.petrutik.smartbuy.gateway.model.ConversationStatus;

public interface UserRequestService {
    void registerUserOrSetStatusToNew(Long chatId);

    void addRequest(Long chatId, ConversationStatus conversationStatus, String clientMessage);

    void listOfAllRequests(Long chatId, ConversationStatus conversationStatus);

    void showRequest(Long chatId, Integer requestNumber);

    void removeRequest(Long chatId, Integer requestNumber);

    void removeAll(Long chatId, ConversationStatus conversationStatus);
}
