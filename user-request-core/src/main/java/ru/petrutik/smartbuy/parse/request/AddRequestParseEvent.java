package ru.petrutik.smartbuy.parse.request;

import java.math.BigDecimal;

public class AddRequestParseEvent {
    private Long requestId;
    private String searchQuery;
    private BigDecimal maxPrice;

    public AddRequestParseEvent() {
    }

    public AddRequestParseEvent(Long requestId, String searchQuery, BigDecimal maxPrice) {
        this.requestId = requestId;
        this.searchQuery = searchQuery;
        this.maxPrice = maxPrice;
    }

    public Long getRequestId() {
        return requestId;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
}
