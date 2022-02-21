package ru.javaprojects.passwordresetservice.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType;
import ru.javaprojects.passwordresetservice.messaging.MessageSender;
import ru.javaprojects.passwordresetservice.service.PasswordResetService;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.*;
import static ru.javaprojects.passwordresetservice.testdata.PasswordResetTokenTestData.*;
import static ru.javaprojects.passwordresetservice.web.AppExceptionHandler.EXCEPTION_INVALID_PASSWORD;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
class PasswordResetRestControllerTest {
    private static final String REST_URL = PasswordResetRestController.REST_URL + '/';

    @Autowired
    private PasswordResetService service;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MessageSender messageSender;

    @Autowired
    private MockMvc mockMvc;

    public ResultMatcher errorType(ErrorType type) {
        return jsonPath("$.type").value(type.name());
    }

    public ResultMatcher detailMessage(String code) {
        return jsonPath("$.details").value(code);
    }

    @PostConstruct
    void setupEmailVerificationService() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method mailSenderSetter = service.getClass().getDeclaredMethod("setMailSender", JavaMailSender.class);
        mailSenderSetter.setAccessible(true);
        mailSenderSetter.invoke(service, mailSender);
        Method messageSenderSetter = service.getClass().getDeclaredMethod("setMessageSender", MessageSender.class);
        messageSenderSetter.setAccessible(true);
        messageSenderSetter.invoke(service, messageSender);
    }


    @Test
    void checkToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(TOKEN_PARAM, notExpiredToken.getToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void checkTokenNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(TOKEN_PARAM, NOT_FOUND_TOKEN))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void checkTokenExpired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(TOKEN_PARAM, expiredToken.getToken()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void resetPassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .param(TOKEN_PARAM, notExpiredToken.getToken())
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(messageSender, Mockito.times(1)).sendChangePasswordMessage(notExpiredToken.getEmail(), NEW_PASSWORD);
    }

    @Test
    void resetPasswordTokenNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .param(TOKEN_PARAM, NOT_FOUND_TOKEN)
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
        Mockito.verify(messageSender, Mockito.times(0)).sendChangePasswordMessage(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void resetPasswordTokenExpired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .param(TOKEN_PARAM, expiredToken.getToken())
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
        Mockito.verify(messageSender, Mockito.times(0)).sendChangePasswordMessage(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void resetPasswordPasswordInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                .param(TOKEN_PARAM, notExpiredToken.getToken())
                .param(PASSWORD_PARAM, "pass"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_INVALID_PASSWORD));
        Mockito.verify(messageSender, Mockito.times(0)).sendChangePasswordMessage(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void wrongRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL + "AAA/BBB/CCC"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(errorType(WRONG_REQUEST));
    }
}