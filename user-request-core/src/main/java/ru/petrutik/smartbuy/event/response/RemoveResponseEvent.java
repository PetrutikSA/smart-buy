package ru.petrutik.smartbuy.event.response;

public class RemoveResponseEvent {
    private Long chatId;
    private Integer requestNumber;

    public RemoveResponseEvent() {
    }

    public RemoveResponseEvent(Long chatId, Integer requestNumber) {
        this.chatId = chatId;
        this.requestNumber = requestNumber;
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
}
