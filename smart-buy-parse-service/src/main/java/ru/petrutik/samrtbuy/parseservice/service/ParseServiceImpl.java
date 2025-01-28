package ru.petrutik.samrtbuy.parseservice.service;

import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.dto.ProductDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ParseServiceImpl implements ParseService {
    private final List<ParseMarketService> parseMarketServices;

    public ParseServiceImpl(List<ParseMarketService> parseMarketServices) {
        this.parseMarketServices = parseMarketServices;
    }

    @Override
    public void addRequestParsing(Long requestId, String searchQuery, BigDecimal maxPrice) {
        List<ProductDto> products = parseQuery(searchQuery, maxPrice);
    }

    private List<ProductDto> parseQuery(String searchQuery, BigDecimal maxPrice) {
        List<ProductDto> products = new ArrayList<>();
        for (ParseMarketService parseMarketService : parseMarketServices) {
            products.addAll(parseMarketService.parseQuery(searchQuery));
        }
        return products.stream()
                .filter(product -> product.getPrice().compareTo(maxPrice) <= 0)
                .sorted(Comparator.comparing(ProductDto::getPrice))
                .limit(5)
                .toList();
    }
}
