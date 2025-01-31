package ru.petrutik.smartbuy.event.user.request;

public class RemoveAllRequestsEvent {
    private Long chatId;

    public RemoveAllRequestsEvent() {
    }

    public RemoveAllRequestsEvent(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
