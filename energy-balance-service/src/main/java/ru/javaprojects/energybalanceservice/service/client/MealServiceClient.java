package ru.javaprojects.energybalanceservice.service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "meal-service")
public interface MealServiceClient {
    Logger logger = LoggerFactory.getLogger(MealServiceClient.class);

    @GetMapping("/api/meals/total-calories")
    @CircuitBreaker(name = "meal-service-get-calories", fallbackMethod = "getInvalidMealCaloriesValue")
    Integer getMealCalories(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    default Integer getInvalidMealCaloriesValue(Exception exception) {
        logger.error("failed to get meal calories from meal-service:" + exception.getLocalizedMessage());
        return -1;
    }
}