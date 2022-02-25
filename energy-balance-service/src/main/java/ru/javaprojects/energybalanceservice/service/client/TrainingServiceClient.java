package ru.javaprojects.energybalanceservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "training-service")
public interface TrainingServiceClient {

    @GetMapping("/api/exercises/total-calories-burned")
    Integer getTrainingCalories(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    //TODO Add fallback method to return -1
}