package ru.petrutik.smartbuy.requestservice.model;

import java.util.List;

public class Request {
    private Long id;
    private String name;
    private String searchQuery;
    private String maxPrice;
    private boolean isUpdated;
    private List<Product> result;
    private List<Product> banned;
}
