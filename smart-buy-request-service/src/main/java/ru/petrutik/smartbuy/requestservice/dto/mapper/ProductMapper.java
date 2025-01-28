package ru.petrutik.smartbuy.requestservice.dto.mapper;

import org.springframework.stereotype.Component;
import ru.petrutik.smartbuy.event.dto.ProductDto;
import ru.petrutik.smartbuy.requestservice.model.Product;

@Component
public class ProductMapper {
    public ProductDto productToProductDto(Product product) {
        return new ProductDto(product.getUrl(), product.getPrice());
    }
}
