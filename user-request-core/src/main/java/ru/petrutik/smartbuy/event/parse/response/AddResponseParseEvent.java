package ru.petrutik.smartbuy.event.parse.response;

import ru.petrutik.smartbuy.event.dto.ProductDto;

import java.util.List;

public class AddResponseParseEvent {
    private Long requestId;
    private List<ProductDto> products;

    public AddResponseParseEvent() {
    }

    public AddResponseParseEvent(Long requestId, List<ProductDto> products) {
        this.requestId = requestId;
        this.products = products;
    }

    public Long getRequestId() {
        return requestId;
    }

    public List<ProductDto> getProducts() {
        return products;
    }
}
