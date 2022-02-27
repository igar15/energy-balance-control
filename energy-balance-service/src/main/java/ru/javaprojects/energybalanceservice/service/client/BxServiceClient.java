package ru.javaprojects.energybalanceservice.service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "bx-service")
public interface BxServiceClient {
    Logger logger = LoggerFactory.getLogger(BxServiceClient.class);

    @GetMapping("/api/bx")
    @CircuitBreaker(name = "bx-service-get-calories", fallbackMethod = "getInvalidBxCaloriesValue")
    Integer getBxCalories(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    default Integer getInvalidBxCaloriesValue(Exception exception) {
        logger.error("failed to get bx calories from bx-service:" + exception.getLocalizedMessage());
        return -1;
    }
}