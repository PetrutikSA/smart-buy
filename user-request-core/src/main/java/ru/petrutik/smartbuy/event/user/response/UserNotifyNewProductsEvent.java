package ru.petrutik.smartbuy.event.user.response;

import ru.petrutik.smartbuy.event.dto.ProductDto;

import java.util.List;
import java.util.Map;

public class UserNotifyNewProductsEvent {
    private Long chatId;
    private Map<String, List<ProductDto>> mapSearchQueryToListNewProducts;

    public UserNotifyNewProductsEvent() {
    }

    public UserNotifyNewProductsEvent(Long chatId, Map<String, List<ProductDto>> mapSearchQueryToListNewProducts) {
        this.chatId = chatId;
        this.mapSearchQueryToListNewProducts = mapSearchQueryToListNewProducts;
    }

    public Long getChatId() {
        return chatId;
    }

    public Map<String, List<ProductDto>> getMapSearchQueryToListNewProducts() {
        return mapSearchQueryToListNewProducts;
    }
}
