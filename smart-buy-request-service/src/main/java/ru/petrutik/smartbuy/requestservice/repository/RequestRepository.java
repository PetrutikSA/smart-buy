package ru.petrutik.smartbuy.requestservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.petrutik.smartbuy.requestservice.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByUserId(Long userId);

    Optional<Request> findByUserIdAndRequestNumber(Long userId, Integer requestNumber);

    void deleteAllByUserId(Long userId);
}
