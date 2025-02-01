package ru.petrutik.samrtbuy.parseservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.dto.ProductDto;
import ru.petrutik.smartbuy.event.parse.response.AddResponseParseEvent;
import ru.petrutik.smartbuy.event.parse.response.UpdateResponseParseEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ParseServiceImpl implements ParseService {
    private final List<ParseMarketService> parseMarketServices;
    private final KafkaTemplate<Long, Object> kafkaTemplate;
    private final String responseTopicName;
    private final Logger logger;

    public ParseServiceImpl(List<ParseMarketService> parseMarketServices,
                            KafkaTemplate<Long, Object> kafkaTemplate,
                            @Value("#{@kafkaConfig.getResponseTopicName()}") String responseTopicName) {
        this.parseMarketServices = parseMarketServices;
        this.kafkaTemplate = kafkaTemplate;
        this.responseTopicName = responseTopicName;
        this.logger = LoggerFactory.getLogger(ParseServiceImpl.class);
    }

    @Override
    public void addRequestParsing(Long requestId, String searchQuery, BigDecimal maxPrice) {
        List<ProductDto> products = parseQuery(searchQuery, maxPrice);
        AddResponseParseEvent addResponseParseEvent = new AddResponseParseEvent(requestId, products);
        sendToKafkaTopic(requestId, addResponseParseEvent);
    }

    @Override
    public void updateRequestParsing(Long requestId, String searchQuery, BigDecimal maxPrice) {
        List<ProductDto> products = parseQuery(searchQuery, maxPrice);
        UpdateResponseParseEvent updateResponseParseEvent = new UpdateResponseParseEvent(requestId, products);
        sendToKafkaTopic(requestId, updateResponseParseEvent);
    }

    private List<ProductDto> parseQuery(String searchQuery, BigDecimal maxPrice) {
        List<ProductDto> products = new ArrayList<>();
        logger.info("Start parsing request");
        for (ParseMarketService parseMarketService : parseMarketServices) {
            logger.info("Start parsing with service {}", parseMarketService.getClass().getName());
            products.addAll(parseMarketService.parseQuery(searchQuery));
        }
        logger.info("Get parsing results: {}", products);
        return products.stream()
                .filter(product -> product.getPrice().compareTo(maxPrice) <= 0)
                .sorted(Comparator.comparing(ProductDto::getPrice))
                .limit(5)
                .toList();
    }

    private void sendToKafkaTopic(Long key, Object value) {
        CompletableFuture<SendResult<Long, Object>> future =
                kafkaTemplate.send(responseTopicName, key, value);
        future.whenComplete(((stringSendResult, throwable) -> {
            if (throwable != null) {
                logger.error("Failed to send message: {}", throwable.getLocalizedMessage(), throwable);
            } else {
                logger.info("Message sent successfully {}", stringSendResult);
            }
        }));
    }
}
