package ru.javaprojects.bxservice.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.bxservice.service.BasicExchangeService;
import ru.javaprojects.bxservice.service.client.UserServiceClient;
import ru.javaprojects.energybalancecontrolshared.test.AbstractControllerTest;
import ru.javaprojects.energybalancecontrolshared.test.WithMockCustomUser;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.bxservice.testdata.BasicExchangeTestData.*;
import static ru.javaprojects.energybalancecontrolshared.test.TestData.*;
import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.UNAUTHORIZED_ERROR;
import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.WRONG_REQUEST;

@Transactional
class BasicExchangeRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = BasicExchangeRestController.REST_URL;

    @Autowired
    private BasicExchangeService service;

    @Mock
    private UserServiceClient userServiceClient;

    @PostConstruct
    private void setupUserServiceClient() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Mockito.when(userServiceClient.getUserBxDetails(USER_ID)).thenReturn(userBxDetails);
        Mockito.when(userServiceClient.getUserBxDetails(ADMIN_ID)).thenReturn(adminBxDetails);
        Method userServiceClientSetter = service.getClass().getDeclaredMethod("setUserServiceClient",  UserServiceClient.class);
        userServiceClientSetter.setAccessible(true);
        userServiceClientSetter.invoke(service, userServiceClient);
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void getBxCalories() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(REST_URL)
                .param(DATE_PARAM, DATE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String bxCalories = actions.andReturn().getResponse().getContentAsString();
        assertEquals(USER_BX_CALORIES, Integer.parseInt(bxCalories));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void getBxCaloriesWhenBasicExchangeDoesNotExist() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(REST_URL)
                .param(DATE_PARAM, LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String bxCalories = actions.andReturn().getResponse().getContentAsString();
        assertEquals(USER_BX_CALORIES, Integer.parseInt(bxCalories));
    }

    @Test
    void getBxCaloriesUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param(DATE_PARAM, DATE))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void wrongRequest() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "AAA/BBB/CCC"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(errorType(WRONG_REQUEST));
    }
}