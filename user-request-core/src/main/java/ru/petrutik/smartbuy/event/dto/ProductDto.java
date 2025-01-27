package ru.petrutik.smartbuy.event.dto;

import java.math.BigDecimal;

public class ProductDto {
    private String url;
    private BigDecimal price;

    public ProductDto() {
    }

    public ProductDto(String url, BigDecimal price) {
        this.url = url;
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
