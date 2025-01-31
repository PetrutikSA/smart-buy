package ru.petrutik.smartbuy.event.user.response;

public class ExceptionResponseEvent {
    private Long chatId;
    private String message;

    public ExceptionResponseEvent() {
    }

    public ExceptionResponseEvent(Long chatId, String message) {
        this.chatId = chatId;
        this.message = message;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getMessage() {
        return message;
    }
}
