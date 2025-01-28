package ru.petrutik.smartbuy.event.response;

public class RemoveResponseEvent {
    private Long chatId;
    private Integer requestNumber;
    private Integer remainRequestsCount;

    public RemoveResponseEvent() {
    }

    public RemoveResponseEvent(Long chatId, Integer requestNumber, Integer remainRequestsCount) {
        this.chatId = chatId;
        this.requestNumber = requestNumber;
        this.remainRequestsCount = remainRequestsCount;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Integer getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(Integer requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Integer getRemainRequestsCount() {
        return remainRequestsCount;
    }

    public void setRemainRequestsCount(Integer remainRequestsCount) {
        this.remainRequestsCount = remainRequestsCount;
    }
}
