package ru.petrutik.smartbuy.gateway.service;

import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.dto.ProductDto;
import ru.petrutik.smartbuy.event.dto.RequestDto;
import ru.petrutik.smartbuy.gateway.model.ConversationStatus;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserResponseServiceImpl implements UserResponseService {
    private final SmartBuyBot bot;
    private final ConversationService conversationService;

    public UserResponseServiceImpl(SmartBuyBot bot, ConversationService conversationService) {
        this.bot = bot;
        this.conversationService = conversationService;
    }

    @Override
    public void listAllResponse(Long chatId, List<RequestDto> requests) {
        StringBuilder responseTextBuilder = new StringBuilder();
        for (RequestDto requestDto : requests) {
            responseTextBuilder.append(requestDto.getRequestNumber());
            responseTextBuilder.append(".) ");
            responseTextBuilder.append(requestDto.getSearchQuery());
            BigDecimal maxPrice = requestDto.getMaxPrice();
            if (maxPrice != null) {
                responseTextBuilder.append(" - цена не более: ");
                responseTextBuilder.append(maxPrice);
            }
            responseTextBuilder.append("\n");
        }
        bot.sendText(chatId, responseTextBuilder.toString());
        if (conversationService.checkConversationStatus(chatId) == ConversationStatus.LIST)
            conversationService.makeConversationStatusNew(chatId);
    }

    @Override
    public void showResponse(Long chatId, String requestQuery, List<ProductDto> products) {

    }

    @Override
    public void updateRequestCount(Long chatId, Integer requestCount) {

    }

    @Override
    public void removeResponse(Long chatId, Integer requestNumber, Integer remainRequestsCount) {

    }

    @Override
    public void removeAllResponse(Long chatId) {

    }

    @Override
    public void exceptionResponse(Long chatId, String message) {

    }
}
