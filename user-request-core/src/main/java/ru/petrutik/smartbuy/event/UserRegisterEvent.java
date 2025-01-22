package ru.petrutik.smartbuy.event;

public class UserRegisterEvent {
    private Long chatId;

    public UserRegisterEvent() {
    }

    public UserRegisterEvent(Long chatId) {
        this.chatId = chatId;
    }
}
