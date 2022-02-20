package ru.javaprojects.energybalanceservice.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.energybalancecontrolshared.test.WithMockCustomUser;
import ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType;
import ru.javaprojects.energybalanceservice.service.EnergyBalanceService;
import ru.javaprojects.energybalanceservice.service.client.BxServiceClient;
import ru.javaprojects.energybalanceservice.service.client.MealServiceClient;
import ru.javaprojects.energybalanceservice.service.client.TrainingServiceClient;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.UNAUTHORIZED_ERROR;
import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.WRONG_REQUEST;
import static ru.javaprojects.energybalanceservice.testdata.EnergyBalanceTestData.*;
import static ru.javaprojects.energybalanceservice.testdata.UserTestData.USER1_ID_STRING;
import static ru.javaprojects.energybalanceservice.testdata.UserTestData.USER_ROLE;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class EnergyBalanceControllerTest {
    private static final String REST_URL = EnergyBalanceController.REST_URL;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnergyBalanceService service;

    @Mock
    private MealServiceClient mealServiceClient;

    @Mock
    private TrainingServiceClient trainingServiceClient;

    @Mock
    private BxServiceClient bxServiceClient;

    private ResultMatcher errorType(ErrorType type) {
        return jsonPath("$.type").value(type.name());
    }

    private ResultMatcher detailMessage(String code) {
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

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void getReport() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(DATE_PARAM, DATE.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(ENERGY_BALANCE_REPORT_MATCHER.contentJson(positiveReport));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void getErrorReport() throws Exception {
        Mockito.when(mealServiceClient.getMealCalories(DATE)).thenReturn(-1);
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(DATE_PARAM, DATE.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(ENERGY_BALANCE_REPORT_MATCHER.contentJson(errorReport));
        Mockito.when(mealServiceClient.getMealCalories(DATE)).thenReturn(MEAL_CALORIES);
    }

    @Test
    void getReportUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(DATE_PARAM, DATE.toString()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING, userRoles = {USER_ROLE})
    void wrongRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "AAA/BBB/CCC"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(errorType(WRONG_REQUEST));
    }
}