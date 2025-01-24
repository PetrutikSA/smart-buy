package ru.petrutik.smartbuy.event.request;

public class ListAllRequestsEvent {
    private Long chatId;

    public ListAllRequestsEvent() {
    }

    public ListAllRequestsEvent(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
}
