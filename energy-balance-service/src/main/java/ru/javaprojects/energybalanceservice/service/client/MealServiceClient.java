package ru.javaprojects.energybalanceservice.service.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

//TODO Make real feign client here
//@FeignClient(name = "meal-service")
public interface MealServiceClient {

    //Check Authorization Header passing to feign client
    //Check @RequestParam date, may be it needs to use formatter
    @GetMapping("/api/meals/total-calories")
    Integer getMealCalories(@RequestParam LocalDate date);

    //TODO Add fallback method to return -1
}