package ru.petrutik.smartbuy.event.dto;

import java.math.BigDecimal;

public class RequestDto {
    private Integer requestNumber;
    private String searchQuery;
    private BigDecimal maxPrice;

    public RequestDto() {
    }

    public RequestDto(Integer requestNumber, String searchQuery, BigDecimal maxPrice) {
        this.requestNumber = requestNumber;
        this.searchQuery = searchQuery;
        this.maxPrice = maxPrice;
    }

    public Integer getRequestNumber() {
        return requestNumber;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
}
