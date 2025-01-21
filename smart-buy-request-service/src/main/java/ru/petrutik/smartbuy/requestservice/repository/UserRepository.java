package ru.petrutik.smartbuy.requestservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.petrutik.smartbuy.requestservice.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
