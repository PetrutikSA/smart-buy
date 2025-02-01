package ru.petrutik.smartbuy.schedulerservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.petrutik.smartbuy.event.scheduler.SchedulerRequestUpdateEvent;
import ru.petrutik.smartbuy.event.scheduler.SchedulerUserNotifyEvent;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class ScheduleService {
    private final String schedulerRequestTopicName;
    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public ScheduleService(@Value("#{@kafkaConfig.getRequestSchedulerRequestTopic()}") String schedulerRequestTopicName,
                           KafkaTemplate<Long, Object> kafkaTemplate) {
        this.schedulerRequestTopicName = schedulerRequestTopicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    private final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Scheduled(cron = "0 0 3 * * *")
    public void updateAllRequests() {
        logger.info("Updating users start");
        Long key = Instant.now().toEpochMilli();
        SchedulerRequestUpdateEvent schedulerRequestUpdateEvent = new SchedulerRequestUpdateEvent(key);
        sendToKafkaTopic(key, schedulerRequestUpdateEvent);

    }

    @Scheduled(cron = "0 30 14 * * *")
    public void notifyUsersWithUpdatedRequests() {
        logger.info("Notifying users start");
        Long key = Instant.now().toEpochMilli();
        SchedulerUserNotifyEvent schedulerUserNotifyEvent = new SchedulerUserNotifyEvent(key);
        sendToKafkaTopic(key, schedulerUserNotifyEvent);
    }

    private void sendToKafkaTopic(Long key, Object value) {
        CompletableFuture<SendResult<Long, Object>> future =
                kafkaTemplate.send(schedulerRequestTopicName, key, value);
        future.whenComplete(((stringSendResult, throwable) -> {
            if (throwable != null) {
                logger.error("Failed to send message: {}", throwable.getLocalizedMessage(), throwable);
            } else {
                logger.info("Message sent successfully {}", stringSendResult);
            }
        }));
    }
}
