package ru.petrutik.smartbuy.schedulerservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {
    private final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Scheduled(cron = "* * * * * *")
    public void updateAllRequests() {
        logger.info("Updating users start");
    }

    @Scheduled(cron = "* * * * * *")
    public void notifyUsersWithUpdatedRequests() {
        logger.info("Notifying users start");
    }
}
