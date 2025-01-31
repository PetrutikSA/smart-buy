package ru.petrutik.smartbuy.event.user.response;

import ru.petrutik.smartbuy.event.dto.ProductDto;

import java.util.List;

public class ShowResultsAfterAddResponseEvent {
    private Long chatId;
    private String requestQuery;
    private List<ProductDto> products;

    public ShowResultsAfterAddResponseEvent() {
    }

    public ShowResultsAfterAddResponseEvent(Long chatId, String requestQuery, List<ProductDto> products) {
        this.chatId = chatId;
        this.requestQuery = requestQuery;
        this.products = products;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getRequestQuery() {
        return requestQuery;
    }

    public List<ProductDto> getProducts() {
        return products;
    }
}
