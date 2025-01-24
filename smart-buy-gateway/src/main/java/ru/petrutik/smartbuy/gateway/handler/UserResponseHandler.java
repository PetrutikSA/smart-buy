package ru.petrutik.smartbuy.gateway.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.petrutik.smartbuy.event.dto.RequestDto;
import ru.petrutik.smartbuy.event.response.ListAllResponseEvent;
import ru.petrutik.smartbuy.gateway.model.ConversationStatus;
import ru.petrutik.smartbuy.gateway.service.SmartBuyBot;
import ru.petrutik.smartbuy.gateway.service.UserRequestService;

import java.math.BigDecimal;
import java.util.List;

@Component
@KafkaListener(topics = "#{@kafkaConfig.getResponseTopicName()}")
public class UserResponseHandler {
    private final SmartBuyBot bot;
    private final UserRequestService userRequestService;

    public UserResponseHandler(SmartBuyBot bot, UserRequestService userRequestService) {
        this.bot = bot;
        this.userRequestService = userRequestService;
    }

    @KafkaHandler
    public void handleListAllResponseEvent(ListAllResponseEvent listAllResponseEvent) {
        List<RequestDto> requests = listAllResponseEvent.getRequests();
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
        Long chatId = listAllResponseEvent.getChatId();
        bot.sendText(chatId, responseTextBuilder.toString());
        if (userRequestService.checkConversationStatus(chatId) == ConversationStatus.LIST)
            userRequestService.makeConversationStatusNew(chatId);
    }
}
