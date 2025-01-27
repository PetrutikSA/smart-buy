package ru.petrutik.smartbuy.requestservice.service;

public interface RequestService {
    void addRequest(Long chatId, String url, Integer price);

    void getAllRequests(Long chatId);

    void showRequest(Long chatId, Integer requestNumber);

    void deleteRequest();

    void deleteAllRequest();
}
