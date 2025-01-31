package ru.petrutik.smartbuy.event.parse.response;

import ru.petrutik.smartbuy.event.dto.ProductDto;

import java.util.List;

public class UpdateResponseParseEvent {
    private Long requestId;
    private List<ProductDto> products;

    public UpdateResponseParseEvent() {
    }

    public UpdateResponseParseEvent(Long requestId, List<ProductDto> products) {
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
