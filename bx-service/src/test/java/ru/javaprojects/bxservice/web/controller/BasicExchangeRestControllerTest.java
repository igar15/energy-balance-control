package ru.javaprojects.bxservice.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.bxservice.service.BasicExchangeService;
import ru.javaprojects.bxservice.service.client.UserServiceClient;
import ru.javaprojects.bxservice.util.exception.ErrorType;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javaprojects.bxservice.testdata.BasicExchangeTestData.*;
import static ru.javaprojects.bxservice.util.exception.ErrorType.UNAUTHORIZED_ERROR;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class BasicExchangeRestControllerTest {
    private static final String REST_URL = BasicExchangeRestController.REST_URL;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BasicExchangeService service;

    @Mock
    private UserServiceClient userServiceClient;

    public ResultMatcher errorType(ErrorType type) {
        return jsonPath("$.type").value(type.name());
    }

    @PostConstruct
    private void setupUserServiceClient() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Mockito.when(userServiceClient.getUserDetails(USER1_ID)).thenReturn(user1Details);
        Mockito.when(userServiceClient.getUserDetails(USER2_ID)).thenReturn(user2Details);
        Method userServiceClientSetter = service.getClass().getDeclaredMethod("setUserServiceClient",  UserServiceClient.class);
        userServiceClientSetter.setAccessible(true);
        userServiceClientSetter.invoke(service, userServiceClient);
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void getBxCalories() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", DATE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String bxCalories = actions.andReturn().getResponse().getContentAsString();
        assertEquals(USER1_BX_CALORIES, Integer.parseInt(bxCalories));
    }

    @Test
    @WithMockCustomUser(userId = USER1_ID_STRING)
    void getBxCaloriesWhenBasicExchangeDoesNotExist() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String bxCalories = actions.andReturn().getResponse().getContentAsString();
        assertEquals(USER1_BX_CALORIES, Integer.parseInt(bxCalories));
    }

    @Test
    void getBxCaloriesUnAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", DATE))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR));
    }
}