package ru.petrutik.smartbuy.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    private final int requestsPerUserLimit;

    public AppConfig(@Value("${smartbuy.app.request-limit}") int requestsPerUserLimit) {
        this.requestsPerUserLimit = requestsPerUserLimit;
    }

    public int getRequestsPerUserLimit() {
        return requestsPerUserLimit;
    }
}
