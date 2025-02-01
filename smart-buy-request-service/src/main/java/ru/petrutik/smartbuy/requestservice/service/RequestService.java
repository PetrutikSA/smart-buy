package ru.petrutik.smartbuy.requestservice.service;

import ru.petrutik.smartbuy.event.dto.ProductDto;

import java.util.List;

public interface RequestService {
    void addRequest(Long chatId, String url, Integer price);

    void getAllRequests(Long chatId);

    void showRequest(Long chatId, Integer requestNumber);

    void removeRequest(Long chatId, Integer requestNumber);

    void removeAllRequest(Long chatId);

    void resultParsingAfterAddRequest(Long requestId, List<ProductDto> products);

    void updateRequestsInitialize();

    void updateRequest(Long requestId, List<ProductDto> productsDto);

    void notifyUsers();
}
