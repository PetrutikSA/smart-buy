package ru.petrutik.smartbuy.event;

public class AddRequestEvent {
    private Long chatId;
    private String url;
    private Integer price;

    public AddRequestEvent() {
    }

    public AddRequestEvent(Long chatId, String url, Integer price) {
        this.chatId = chatId;
        this.url = url;
        this.price = price;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
