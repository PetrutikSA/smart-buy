package ru.petrutik.smartbuy.event;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class UserRegisterEvent {
    private Long chatId;

    public UserRegisterEvent() {
    }

    public UserRegisterEvent(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
