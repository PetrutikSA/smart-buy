package ru.petrutik.smartbuy.requestservice.model;

import java.math.BigDecimal;
import java.util.List;

public class Request {
    private Long id;
    private String name;
    private String searchQuery;
    private BigDecimal maxPrice;
    private boolean isUpdated;
    private List<Product> result;
    private List<Product> banned;
}
