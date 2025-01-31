package ru.petrutik.smartbuy.requestservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import ru.petrutik.smartbuy.event.scheduler.SchedulerRequestUpdateEvent;
import ru.petrutik.smartbuy.event.scheduler.SchedulerUserNotifyEvent;
import ru.petrutik.smartbuy.requestservice.service.RequestService;

@KafkaListener(topics = "#{@kafkaConfig.getSchedulerRequestTopicName()}")
public class SchedulerRequestHandler {
    private final RequestService requestService;
    private final Logger logger;

    public SchedulerRequestHandler(RequestService requestService) {
        this.requestService = requestService;
        this.logger = LoggerFactory.getLogger(SchedulerRequestHandler.class);
    }

    @KafkaHandler
    public void handleSchedulerRequestUpdateEvent(SchedulerRequestUpdateEvent schedulerRequestUpdateEvent) {
        logEventReceiving(schedulerRequestUpdateEvent.getClass().getName());
        requestService.updateRequests();
    }

    @KafkaHandler
    public void handleSchedulerUserNotifyEvent(SchedulerUserNotifyEvent schedulerUserNotifyEvent) {
        logEventReceiving(schedulerUserNotifyEvent.getClass().getName());
        requestService.notifyUsers();
    }

    private void logEventReceiving(String eventClassName) {
        logger.info("Received {}", eventClassName);
    }
}
