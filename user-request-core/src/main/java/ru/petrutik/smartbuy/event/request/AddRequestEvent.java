package ru.petrutik.smartbuy.event.request;

public class AddRequestEvent {
    private Long chatId;
    private String searchQuery;
    private Integer maxPrice;

    public AddRequestEvent() {
    }

    public AddRequestEvent(Long chatId, String url, Integer price) {
        this.chatId = chatId;
        this.searchQuery = url;
        this.maxPrice = price;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }
}
