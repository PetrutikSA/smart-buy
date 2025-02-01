package ru.petrutik.smartbuy.event.parse.request;

import java.math.BigDecimal;

public class UpdateRequestParseEvent {
    private Long requestId;
    private String searchQuery;
    private BigDecimal maxPrice;

    public UpdateRequestParseEvent() {
    }

    public UpdateRequestParseEvent(Long requestId, String searchQuery, BigDecimal maxPrice) {
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
