package ru.petrutik.smartbuy.gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.dto.ProductDto;
import ru.petrutik.smartbuy.event.dto.RequestDto;
import ru.petrutik.smartbuy.gateway.model.Conversation;
import ru.petrutik.smartbuy.gateway.model.ConversationStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class UserResponseServiceImpl implements UserResponseService {
    private final SmartBuyBot bot;
    private final ConversationService conversationService;
    private final Logger logger;

    public UserResponseServiceImpl(SmartBuyBot bot, ConversationService conversationService) {
        this.bot = bot;
        this.conversationService = conversationService;
        this.logger = LoggerFactory.getLogger(UserResponseServiceImpl.class);
    }

    @Override
    public void updateRequestCount(Long chatId, Integer requestCount) {
        Conversation conversation = conversationService.getConversationOrRegisterNew(chatId);
        if (conversation.getRequestAdded() != requestCount) {
            conversation.setRequestAdded(requestCount);
            conversationService.updateConversation(conversation);
        }
    }

    @Override
    public void listAllResponse(Long chatId, List<RequestDto> requests) {
        String message;
        if (requests.isEmpty()) {
            message = "У вас нет ни одного сохраненного запроса";
        } else {
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
            message = responseTextBuilder.toString();
        }
        bot.sendText(chatId, message);
        if (conversationService.checkConversationStatus(chatId) == ConversationStatus.LIST || requests.isEmpty())
            conversationService.makeConversationStatusNew(chatId);
    }

    @Override
    public void showResponse(Long chatId, String requestQuery, List<ProductDto> products) {
        String message;
        if (products.isEmpty()) {
            message = "К сожалению по данному запросу пока нет результатов :(";
        } else {
            StringBuilder responseTextBuilder = new StringBuilder();
            responseTextBuilder.append("По запросу \" ");
            responseTextBuilder.append(requestQuery);
            responseTextBuilder.append("\" найдено:\n");
            for (ProductDto productDto : products) {
                responseTextBuilder.append("---------------\n");
                responseTextBuilder.append("Стоимость: ");
                responseTextBuilder.append(productDto.getPrice());
                responseTextBuilder.append(" руб.\n");
                responseTextBuilder.append("Ссылка: ");
                responseTextBuilder.append(productDto.getUrl());
                responseTextBuilder.append("\n");
            }
            message = responseTextBuilder.toString();
        }
        bot.sendText(chatId, message);
        conversationService.makeConversationStatusNew(chatId);
    }

    @Override
    public void showResultsAfterAddNewRequest(Long chatId, String requestQuery, List<ProductDto> products) {
        if (products != null && !products.isEmpty()) {
            bot.sendText(chatId, "Результаты по добавленному Вами запросу");
            showResponse(chatId, requestQuery, products);
        } else {
            logger.error("Received empty product list as result after add new request, chatId = {}, request query = {}",
                    chatId, requestQuery);
        }
    }

    @Override
    public void removeResponse(Long chatId, Integer requestNumber, Integer remainRequestsCount) {
        String message = "Удален поисковый запрос под номером - " + requestNumber;
        bot.sendText(chatId, message);
        Conversation conversation = conversationService.getConversationOrRegisterNew(chatId);
        conversation.setRequestAdded(remainRequestsCount);
        conversation.setStatus(ConversationStatus.NEW);
        conversationService.updateConversation(conversation);
    }

    @Override
    public void removeAllResponse(Long chatId) {
        String message = "Все поисковые запросы были удалены";
        bot.sendText(chatId, message);
        Conversation conversation = conversationService.getConversationOrRegisterNew(chatId);
        conversation.setRequestAdded(0);
        conversation.setStatus(ConversationStatus.NEW);
        conversationService.updateConversation(conversation);
    }

    @Override
    public void exceptionResponse(Long chatId, String message) {
        bot.sendText(chatId, message);
        conversationService.makeConversationStatusNew(chatId);
    }

    @Override
    public void notifyNewProduct(Long chatId, Map<String, List<ProductDto>> mapSearchQueryToListNewProducts) {
        if (mapSearchQueryToListNewProducts != null && !mapSearchQueryToListNewProducts.isEmpty()) {
            StringBuilder responseTextBuilder = new StringBuilder();
            responseTextBuilder.append("По следующим запросам добавлены новые результаты поиска: \n");
            for (Map.Entry<String, List<ProductDto>> requestProducts : mapSearchQueryToListNewProducts.entrySet()) {
                responseTextBuilder.append("По запросу: \"");
                responseTextBuilder.append(requestProducts.getKey());
                responseTextBuilder.append("\" добавлены продукты:\n");
                for (ProductDto productDto : requestProducts.getValue()) {
                    responseTextBuilder.append("---------------\n");
                    responseTextBuilder.append("Стоимость: ");
                    responseTextBuilder.append(productDto.getPrice());
                    responseTextBuilder.append(" руб.\n");
                    responseTextBuilder.append("Ссылка: ");
                    responseTextBuilder.append(productDto.getUrl());
                    responseTextBuilder.append("\n");
                }
                responseTextBuilder.append("\n");
            }
            bot.sendText(chatId, responseTextBuilder.toString());
        } else {
            logger.error("Received empty map of updated requests, chatId = {}", chatId);
        }
    }
}
