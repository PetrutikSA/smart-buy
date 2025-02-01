package ru.petrutik.smartbuy.requestservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.petrutik.smartbuy.event.parse.response.AddResponseParseEvent;
import ru.petrutik.smartbuy.event.parse.response.UpdateResponseParseEvent;
import ru.petrutik.smartbuy.requestservice.service.RequestService;

@Component
@KafkaListener(topics = "#{@kafkaConfig.getParseResponseTopicName()}")
public class ParseResponseHandler {
    private final RequestService requestService;
    private final Logger logger;

    public ParseResponseHandler(RequestService requestService) {
        this.requestService = requestService;
        this.logger = LoggerFactory.getLogger(ParseResponseHandler.class);
    }

    @KafkaHandler
    public void handleAddResponseParseEvent(AddResponseParseEvent addResponseParseEvent) {
        logEventReceiving(addResponseParseEvent.getClass().getName(), addResponseParseEvent.getRequestId());
        requestService.resultParsingAfterAddRequest(addResponseParseEvent.getRequestId(),
                addResponseParseEvent.getProducts());
    }

    @KafkaHandler
    public void handleUpdateResponseParseEvent(UpdateResponseParseEvent updateResponseParseEvent) {
        logEventReceiving(updateResponseParseEvent.getClass().getName(), updateResponseParseEvent.getRequestId());
        requestService.updateRequest(updateResponseParseEvent.getRequestId(),
                updateResponseParseEvent.getProducts());
    }

    private void logEventReceiving(String eventClassName, Long requestId) {
        logger.info("Received {}, request id = {}", eventClassName, requestId);
    }
}
