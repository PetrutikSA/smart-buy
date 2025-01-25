package ru.petrutik.smartbuy.requestservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.requestservice.model.User;
import ru.petrutik.smartbuy.requestservice.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Logger logger;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.logger = LoggerFactory.getLogger(UserServiceImpl.class);
    }

    @Override
    public User registerUser(Long chatId) {
        User user = new User();
        user.setChatId(chatId);
        logger.info("User created: {}", user);
        user = userRepository.save(user);
        logger.info("User saved in DB: {}", user);
        return user;
    }

    @Override
    public User getUserByChatId(Long chatId) {
        logger.info("Getting user by chatId = {}", chatId);
        Optional<User> optionalUser = userRepository.findByChatId(chatId);
        return optionalUser.orElseGet(() -> registerUser(chatId));
    }

    @Override
    public User updateUser(User user) {
        user = userRepository.save(user);
        logger.info("User updated in DB: {}", user);
        return user;
    }
}
