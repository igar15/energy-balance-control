package ru.javaprojects.energybalanceservice.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.javaprojects.energybalanceservice.model.EnergyBalanceReport;
import ru.javaprojects.energybalanceservice.service.client.BxServiceClient;
import ru.javaprojects.energybalanceservice.service.client.MealServiceClient;
import ru.javaprojects.energybalanceservice.service.client.TrainingServiceClient;
import ru.javaprojects.energybalanceservice.util.EnergyBalanceUtil;

import java.time.LocalDate;
@Service
public class EnergyBalanceService {
    private final MealServiceClient mealServiceClient;
    private final TrainingServiceClient trainingServiceClient;
    private final BxServiceClient bxServiceClient;

    public EnergyBalanceService(MealServiceClient mealServiceClient, TrainingServiceClient trainingServiceClient,
                                BxServiceClient bxServiceClient) {
        this.mealServiceClient = mealServiceClient;
        this.trainingServiceClient = trainingServiceClient;
        this.bxServiceClient = bxServiceClient;
    }

    public EnergyBalanceReport getReport(LocalDate date) {
        Assert.notNull(date, "date must not be null");
        Integer mealCalories = mealServiceClient.getMealCalories(date);
        Integer trainingCalories = trainingServiceClient.getTrainingCalories(date);
        Integer bxCalories = bxServiceClient.getBxCalories(date);
        return EnergyBalanceUtil.makeReport(date, mealCalories, trainingCalories, bxCalories);
    }
}