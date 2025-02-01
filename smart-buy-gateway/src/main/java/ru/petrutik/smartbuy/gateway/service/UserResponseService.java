package ru.petrutik.smartbuy.gateway.service;

import ru.petrutik.smartbuy.event.dto.ProductDto;
import ru.petrutik.smartbuy.event.dto.RequestDto;

import java.util.List;
import java.util.Map;

public interface UserResponseService {
    void listAllResponse(Long chatId, List<RequestDto> requests);

    void showResponse(Long chatId, String requestQuery, List<ProductDto> products);

    void showResultsAfterAddNewRequest(Long chatId, String requestQuery, List<ProductDto> products);

    void updateRequestCount(Long chatId, Integer requestCount);

    void removeResponse(Long chatId, Integer requestNumber, Integer remainRequestsCount);

    void removeAllResponse(Long chatId);

    void exceptionResponse(Long chatId, String message);

    void notifyNewProduct(Long chatId, Map<String, List<ProductDto>> mapSearchQueryToListNewProducts);
}
