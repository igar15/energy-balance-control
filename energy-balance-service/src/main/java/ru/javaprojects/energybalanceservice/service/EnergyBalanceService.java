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

    //TODO Autowired real FeignClients
    private MealServiceClient mealServiceClient = (date) -> 1900;
    private TrainingServiceClient trainingServiceClient = (date) -> 150;
    private BxServiceClient bxServiceClient = (date) -> 1700;

    public EnergyBalanceReport getReport(LocalDate date) {
        Assert.notNull(date, "date must not be null");
        Integer mealCalories = mealServiceClient.getMealCalories(date);
        Integer trainingCalories = trainingServiceClient.getTrainingCalories(date);
        Integer bxCalories = bxServiceClient.getBxCalories(date);
        return EnergyBalanceUtil.makeReport(date, mealCalories, trainingCalories, bxCalories);
    }
}