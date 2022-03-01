package ru.javaprojects.passwordresetservice.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.energybalancecontrolshared.test.AbstractControllerTest;
import ru.javaprojects.passwordresetservice.messaging.MessageSender;
import ru.javaprojects.passwordresetservice.service.PasswordResetService;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.*;
import static ru.javaprojects.passwordresetservice.testdata.PasswordResetTokenTestData.*;
import static ru.javaprojects.passwordresetservice.web.AppExceptionHandler.EXCEPTION_INVALID_PASSWORD;

@Transactional
class PasswordResetRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = PasswordResetRestController.REST_URL + '/';

    @Autowired
    private PasswordResetService service;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MessageSender messageSender;

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
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param(TOKEN_PARAM, notExpiredToken.getToken()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void checkTokenNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param(TOKEN_PARAM, NOT_FOUND_TOKEN))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void checkTokenExpired() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param(TOKEN_PARAM, expiredToken.getToken()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void resetPassword() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param(TOKEN_PARAM, notExpiredToken.getToken())
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(messageSender, Mockito.times(1)).sendPasswordChangedMessage(notExpiredToken.getEmail(), NEW_PASSWORD);
    }

    @Test
    void resetPasswordTokenNotFound() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param(TOKEN_PARAM, NOT_FOUND_TOKEN)
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
        Mockito.verify(messageSender, Mockito.times(0)).sendPasswordChangedMessage(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void resetPasswordTokenExpired() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param(TOKEN_PARAM, expiredToken.getToken())
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
        Mockito.verify(messageSender, Mockito.times(0)).sendPasswordChangedMessage(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void resetPasswordPasswordInvalid() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param(TOKEN_PARAM, notExpiredToken.getToken())
                .param(PASSWORD_PARAM, "pass"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_INVALID_PASSWORD));
        Mockito.verify(messageSender, Mockito.times(0)).sendPasswordChangedMessage(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void wrongRequest() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "AAA/BBB/CCC"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(errorType(WRONG_REQUEST));
    }
}