package ru.petrutik.smartbuy.requestservice.service;

public interface RequestService {
    void addRequest(Long chatId, String url, Integer price);

    void getAllRequests();

    void getRequest();

    void deleteRequest();

    void deleteAllRequest();
}
