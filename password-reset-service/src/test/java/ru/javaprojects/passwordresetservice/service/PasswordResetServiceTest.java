package ru.javaprojects.passwordresetservice.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.javaprojects.passwordresetservice.messaging.MessageSender;
import ru.javaprojects.passwordresetservice.model.PasswordResetToken;
import ru.javaprojects.passwordresetservice.repository.PasswordResetTokenRepository;
import ru.javaprojects.passwordresetservice.util.exception.NotFoundException;
import ru.javaprojects.passwordresetservice.util.exception.PasswordResetException;

import javax.annotation.PostConstruct;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.passwordresetservice.testdata.PasswordResetTokenTestData.*;

@SpringBootTest
@ActiveProfiles("dev")
@Sql(scripts = "classpath:data.sql", config = @SqlConfig(encoding = "UTF-8"))
class PasswordResetServiceTest {

    @Autowired
    private PasswordResetService service;

    @Autowired
    private PasswordResetTokenRepository repository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MessageSender messageSender;

    @PostConstruct
    void setupEmailVerificationService() {
        service.setMailSender(mailSender);
        service.setMessageSender(messageSender);
    }

    @Test
    void sendPasswordResetEmail() {
        service.sendPasswordResetEmail(USER_EMAIL);
        Mockito.verify(mailSender, Mockito.times(1)).send(Mockito.any(SimpleMailMessage.class));
        PasswordResetToken passwordResetToken = repository.findByEmail(USER_EMAIL).get();
        assertTrue(passwordResetToken.getExpiryDate().after(new Date()));
    }

    @Test
    void sendPasswordResetEmailWhenTokenAlreadyExist() {
        service.sendPasswordResetEmail(notExpiredToken.getEmail());
        Mockito.verify(mailSender, Mockito.times(1)).send(Mockito.any(SimpleMailMessage.class));
        PasswordResetToken passwordResetToken = repository.findByEmail(notExpiredToken.getEmail()).get();
        assertTrue(passwordResetToken.getExpiryDate().after(new Date()));
        assertNotEquals(notExpiredToken.getToken(), passwordResetToken.getToken());
    }

    @Test
    void checkToken() {
        assertDoesNotThrow(() -> service.checkToken(notExpiredToken.getToken()));
    }

    @Test
    void checkTokenNotFound() {
        assertThrows(NotFoundException.class, () -> service.checkToken(NOT_FOUND_TOKEN));
    }

    @Test
    void checkTokenExpired() {
        assertThrows(PasswordResetException.class, () -> service.checkToken(expiredToken.getToken()));
    }

    @Test
    void resetPassword() {
        service.resetPassword(notExpiredToken.getToken(), NEW_PASSWORD);
        Mockito.verify(messageSender, Mockito.times(1)).sendChangePasswordMessage(notExpiredToken.getEmail(), NEW_PASSWORD);
        assertTrue(repository.findByToken(notExpiredToken.getToken()).isEmpty());
    }

    @Test
    void resetPasswordNotFoundToken() {
        assertThrows(NotFoundException.class, () -> service.resetPassword(NOT_FOUND_TOKEN, NEW_PASSWORD));
        Mockito.verify(messageSender, Mockito.times(0)).sendChangePasswordMessage(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void resetPasswordTokenExpired() {
        assertThrows(PasswordResetException.class, () -> service.resetPassword(expiredToken.getToken(), NEW_PASSWORD));
        Mockito.verify(messageSender, Mockito.times(0)).sendChangePasswordMessage(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void delete() {
        service.delete(notExpiredToken.getEmail());
        assertTrue(repository.findByEmail(notExpiredToken.getEmail()).isEmpty());
    }
}