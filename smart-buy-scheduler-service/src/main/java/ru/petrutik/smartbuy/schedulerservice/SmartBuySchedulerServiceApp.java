package ru.petrutik.smartbuy.schedulerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartBuySchedulerServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(SmartBuySchedulerServiceApp.class, args);
    }
}
