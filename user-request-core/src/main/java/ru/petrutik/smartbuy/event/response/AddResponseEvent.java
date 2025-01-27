package ru.petrutik.smartbuy.event.response;

public class AddResponseEvent {
    private Long chatId;
    private Integer remainRequestsCount;

    public AddResponseEvent() {
    }

    public AddResponseEvent(Long chatId, Integer remainRequestsCount) {
        this.chatId = chatId;
        this.remainRequestsCount = remainRequestsCount;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Integer getRemainRequestsCount() {
        return remainRequestsCount;
    }

    public void setRemainRequestsCount(Integer remainRequestsCount) {
        this.remainRequestsCount = remainRequestsCount;
    }
}
