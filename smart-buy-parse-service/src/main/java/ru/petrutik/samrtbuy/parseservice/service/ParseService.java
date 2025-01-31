package ru.petrutik.samrtbuy.parseservice.service;

import java.math.BigDecimal;

public interface ParseService {
    void addRequestParsing(Long requestId, String searchQuery, BigDecimal maxPrice);

    void updateRequestParsing(Long requestId, String searchQuery, BigDecimal maxPrice);
}
