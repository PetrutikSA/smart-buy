package ru.petrutik.smartbuy.requestservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.petrutik.smartbuy.requestservice.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByChatId(Long chatId);
}
