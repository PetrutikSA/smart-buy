package ru.petrutik.samrtbuy.parseservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.petrutik.samrtbuy.parseservice.service.ParseService;
import ru.petrutik.smartbuy.event.parse.request.AddRequestParseEvent;

@Component
@KafkaListener(topics = "#{@kafkaConfig.getRequestTopicName()}")
public class ParseRequestHandler {
    private final ParseService parseService;
    private final Logger logger;

    public ParseRequestHandler(ParseService parseService) {
        this.parseService = parseService;
        this.logger = LoggerFactory.getLogger(ParseRequestHandler.class);
    }

    @KafkaHandler
    public void handleAddRequestParseEvent(AddRequestParseEvent addRequestParseEvent) {
        logEventReceiving(addRequestParseEvent.getClass().getName(), addRequestParseEvent.getRequestId());
        parseService.addRequestParsing(addRequestParseEvent.getRequestId(), addRequestParseEvent.getSearchQuery(),
                addRequestParseEvent.getMaxPrice());
    }

    private void logEventReceiving(String eventClassName, Long requestId) {
        logger.info("Received {}, request id = {}", eventClassName, requestId);
    }
}
