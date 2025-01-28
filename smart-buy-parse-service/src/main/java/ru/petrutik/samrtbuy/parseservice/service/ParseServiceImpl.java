package ru.petrutik.samrtbuy.parseservice.service;

import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.dto.ProductDto;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ParseServiceImpl implements ParseService {
    @Override
    public void addRequestParsing(Long requestId, String searchQuery, BigDecimal maxPrice) {

    }

    private List<ProductDto> parseQuery(String searchQuery, BigDecimal maxPrice) {
        return null;
    }
}
