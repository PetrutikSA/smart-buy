package ru.petrutik.smartbuy.event.user.response;

import ru.petrutik.smartbuy.event.dto.RequestDto;

import java.util.List;

public class ListAllResponseEvent {
    private Long chatId;
    private List<RequestDto> requests;

    public ListAllResponseEvent() {
    }

    public ListAllResponseEvent(Long chatId, List<RequestDto> requests) {
        this.chatId = chatId;
        this.requests = requests;
    }

    public Long getChatId() {
        return chatId;
    }

    public List<RequestDto> getRequests() {
        return requests;
    }
}
