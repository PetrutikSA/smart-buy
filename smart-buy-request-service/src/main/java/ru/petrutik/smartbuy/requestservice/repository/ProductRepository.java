package ru.petrutik.smartbuy.requestservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.petrutik.smartbuy.requestservice.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByRequestIdAndIsBanned(Long requestId, boolean isBanned);

    List<Product> findAllByIsNew(boolean isNew);
}
