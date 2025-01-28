package ru.petrutik.samrtbuy.parseservice.service;

import ru.petrutik.smartbuy.event.dto.ProductDto;

import java.util.List;

public interface ParseMarketService {
    List<ProductDto> parseQuery(String searchQuery);
}
