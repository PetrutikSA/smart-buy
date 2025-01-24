package ru.petrutik.smartbuy.requestservice.service;

import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.requestservice.model.User;
import ru.petrutik.smartbuy.requestservice.repository.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(Long chatId) {
        User user = new User();
        user.setChatId(chatId);
        user = userRepository.save(user);
        return user;
    }

    @Override
    public User getUserByChatId(Long chatId) {
        Optional<User> optionalUser = userRepository.findByChatId(chatId);
        return optionalUser.orElse(registerUser(chatId));
    }

    @Override
    public User updateUser(User user) {
        user = userRepository.save(user);
        return user;
    }
}
