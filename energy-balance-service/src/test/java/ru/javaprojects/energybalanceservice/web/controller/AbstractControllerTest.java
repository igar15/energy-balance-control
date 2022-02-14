package ru.javaprojects.energybalanceservice.web.controller;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.javaprojects.energybalanceservice.service.EnergyBalanceService;
import ru.javaprojects.energybalanceservice.service.client.BxServiceClient;
import ru.javaprojects.energybalanceservice.service.client.MealServiceClient;
import ru.javaprojects.energybalanceservice.service.client.TrainingServiceClient;
import ru.javaprojects.energybalanceservice.util.exception.ErrorType;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static ru.javaprojects.energybalanceservice.testdata.EnergyBalanceTestData.*;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public abstract class AbstractControllerTest {
    protected static final String REST_URL = EnergyBalanceController.REST_URL;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected EnergyBalanceService service;

    @Mock
    protected MealServiceClient mealServiceClient;

    @Mock
    protected TrainingServiceClient trainingServiceClient;

    @Mock
    protected BxServiceClient bxServiceClient;

    protected ResultMatcher errorType(ErrorType type) {
        return jsonPath("$.type").value(type.name());
    }

    protected ResultMatcher detailMessage(String code) {
        return jsonPath("$.details").value(code);
    }

    @PostConstruct
    private void setupServiceClients() throws IllegalAccessException, NoSuchFieldException {
        Mockito.when(mealServiceClient.getMealCalories(DATE)).thenReturn(MEAL_CALORIES);
        Mockito.when(trainingServiceClient.getTrainingCalories(DATE)).thenReturn(TRAINING_CALORIES);
        Mockito.when(bxServiceClient.getBxCalories(DATE)).thenReturn(BX_CALORIES);
        Field mealServiceClientField = service.getClass().getDeclaredField("mealServiceClient");
        mealServiceClientField.setAccessible(true);
        mealServiceClientField.set(service, mealServiceClient);
        Field trainingServiceClientField = service.getClass().getDeclaredField("trainingServiceClient");
        trainingServiceClientField.setAccessible(true);
        trainingServiceClientField.set(service, trainingServiceClient);
        Field bxServiceClientField = service.getClass().getDeclaredField("bxServiceClient");
        bxServiceClientField.setAccessible(true);
        bxServiceClientField.set(service, bxServiceClient);
    }
}