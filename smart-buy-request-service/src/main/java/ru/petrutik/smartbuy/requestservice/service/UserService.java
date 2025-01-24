package ru.petrutik.smartbuy.requestservice.service;

import ru.petrutik.smartbuy.requestservice.model.User;

public interface UserService {
    User registerUser(Long chatId);

    User getUserByChatId(Long chatId);

    User updateUser(User user);
}
