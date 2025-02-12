package ru.petrutik.smartbuy.requestservice.dto.mapper;

import org.springframework.stereotype.Component;
import ru.petrutik.smartbuy.event.dto.ProductDto;
import ru.petrutik.smartbuy.requestservice.model.Product;
import ru.petrutik.smartbuy.requestservice.model.Request;

@Component
public class ProductMapper {
    public ProductDto productToProductDto(Product product) {
        return new ProductDto(product.getUrl(), product.getPrice());
    }

    public Product productDtoToProduct(ProductDto productDto, Request request, boolean isNew, boolean isBanned) {
        Product product = new Product();
        product.setUrl(productDto.getUrl());
        product.setPrice(productDto.getPrice());
        product.setNew(isNew);
        product.setBanned(isBanned);
        product.setRequest(request);
        return product;
    }
}
