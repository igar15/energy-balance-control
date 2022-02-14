package ru.javaprojects.energybalanceservice.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.energybalanceservice.EnergyBalanceReportMatcher;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.energybalanceservice.testdata.EnergyBalanceTestData.*;
import static ru.javaprojects.energybalanceservice.util.exception.ErrorType.UNAUTHORIZED_ERROR;

class EnergyBalanceControllerTest extends AbstractControllerTest {

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void getReport() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", DATE.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EnergyBalanceReportMatcher.contentJson(positiveReport));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void getErrorReport() throws Exception {
        Mockito.when(mealServiceClient.getMealCalories(DATE)).thenReturn(-1);
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", DATE.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(EnergyBalanceReportMatcher.contentJson(errorReport));
        Mockito.when(mealServiceClient.getMealCalories(DATE)).thenReturn(MEAL_CALORIES);
    }

    @Test
    void getReportUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", DATE.toString()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }
}