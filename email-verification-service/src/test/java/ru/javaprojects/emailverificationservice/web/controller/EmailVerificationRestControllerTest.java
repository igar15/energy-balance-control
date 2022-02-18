package ru.javaprojects.emailverificationservice.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.emailverificationservice.messaging.MessageSender;
import ru.javaprojects.emailverificationservice.model.VerificationToken;
import ru.javaprojects.emailverificationservice.repository.VerificationTokenRepository;
import ru.javaprojects.emailverificationservice.service.EmailVerificationService;
import ru.javaprojects.emailverificationservice.util.exception.ErrorType;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.emailverificationservice.testdata.VerificationTokenTestData.*;
import static ru.javaprojects.emailverificationservice.util.exception.ErrorType.DATA_NOT_FOUND;
import static ru.javaprojects.emailverificationservice.util.exception.ErrorType.VALIDATION_ERROR;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class EmailVerificationRestControllerTest {
    private static final String REST_URL = EmailVerificationRestController.REST_URL + '/';

    @Autowired
    private EmailVerificationService service;

    @Autowired
    private VerificationTokenRepository repository;

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
    void verifyEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + notExpiredNotVerifiedToken.getToken()))
                .andDo(print())
                .andExpect(status().isOk());
        Mockito.verify(messageSender, Mockito.times(1)).sendEmailVerifiedMessage(notExpiredNotVerifiedToken.getEmail());
        VerificationToken verificationToken = repository.findByEmail(notExpiredNotVerifiedToken.getEmail()).get();
        assertTrue(verificationToken.isEmailVerified());
    }

    @Test
    void verifyEmailNotFoundToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + NOT_FOUND_TOKEN))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND))
                .andExpect(detailMessage("Not found verification record with token=" + NOT_FOUND_TOKEN));
        Mockito.verify(messageSender, Mockito.times(0)).sendEmailVerifiedMessage(Mockito.anyString());
    }

    @Test
    void verifyEmailWhenTokenExpired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + expiredToken.getToken()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage("Verification token for " +expiredToken.getEmail() + " expired"));
        Mockito.verify(messageSender, Mockito.times(0)).sendEmailVerifiedMessage(Mockito.anyString());
    }

    @Test
    void verifyEmailWhenAlreadyVerified() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL + alreadyVerifiedToken.getToken()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage("Email already verified:" + alreadyVerifiedToken.getEmail()));
        Mockito.verify(messageSender, Mockito.times(0)).sendEmailVerifiedMessage(Mockito.anyString());
    }
}