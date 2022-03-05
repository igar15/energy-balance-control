package ru.javaprojects.energybalanceservice.service.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "gateway-server", contextId = "trainingServiceClient")
public interface TrainingServiceClient {
    Logger logger = LoggerFactory.getLogger(TrainingServiceClient.class);

    @GetMapping("/training-service/api/exercises/total-calories-burned")
    @CircuitBreaker(name = "training-service-get-calories", fallbackMethod = "getInvalidTrainingCaloriesValue")
    Integer getTrainingCalories(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    default Integer getInvalidTrainingCaloriesValue(Exception exception) {
        logger.error("failed to get training calories from training-service:" + exception.getLocalizedMessage());
        return -1;
    }
}