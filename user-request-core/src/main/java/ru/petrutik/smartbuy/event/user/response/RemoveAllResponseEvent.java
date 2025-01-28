package ru.petrutik.smartbuy.event.user.response;

public class RemoveAllResponseEvent {
    private Long chatId;

    public RemoveAllResponseEvent() {
    }

    public RemoveAllResponseEvent(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
