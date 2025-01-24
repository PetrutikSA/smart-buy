package ru.petrutik.smartbuy.requestservice.service;

import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.requestservice.model.User;
import ru.petrutik.smartbuy.requestservice.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(Long chatId) {
        User user = new User();
        user.setChatId(chatId);
        userRepository.save(user);
    }
}
