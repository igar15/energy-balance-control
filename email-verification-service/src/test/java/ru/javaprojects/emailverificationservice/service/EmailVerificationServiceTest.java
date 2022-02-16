package ru.javaprojects.emailverificationservice.service;

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
import ru.javaprojects.emailverificationservice.messaging.MessageSender;
import ru.javaprojects.emailverificationservice.model.VerificationToken;
import ru.javaprojects.emailverificationservice.repository.VerificationTokenRepository;
import ru.javaprojects.emailverificationservice.util.exception.EmailVerificationException;

import javax.annotation.PostConstruct;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.emailverificationservice.testdata.VerificationTokenTestData.*;

@SpringBootTest
@ActiveProfiles("dev")
@Sql(scripts = "classpath:data.sql", config = @SqlConfig(encoding = "UTF-8"))
class EmailVerificationServiceTest {

    @Autowired
    private EmailVerificationService service;

    @Autowired
    private VerificationTokenRepository repository;

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
    void sendVerificationEmail() {
        service.sendVerificationEmail(NEW_USER_EMAIL);
        Mockito.verify(mailSender, Mockito.times(1)).send(Mockito.any(SimpleMailMessage.class));
        VerificationToken verificationToken = repository.findByEmail(NEW_USER_EMAIL).get();
        assertFalse(verificationToken.isEmailVerified());
        assertTrue(verificationToken.getExpiryDate().after(new Date()));
    }

    @Test
    void sendVerificationEmailWhenAlreadyVerified() {
        assertThrows(EmailVerificationException.class, () -> service.sendVerificationEmail(alreadyVerifiedToken.getEmail()));
        Mockito.verify(mailSender, Mockito.times(0)).send(Mockito.any(SimpleMailMessage.class));
    }

    @Test
    void sendVerificationEmailWhenEmailExistButNotVerified() {
        assertDoesNotThrow(() -> service.sendVerificationEmail(notExpiredNotVerifiedToken.getEmail()));
        Mockito.verify(mailSender, Mockito.times(1)).send(Mockito.any(SimpleMailMessage.class));
        VerificationToken verificationToken = repository.findByEmail(notExpiredNotVerifiedToken.getEmail()).get();
        assertFalse(verificationToken.isEmailVerified());
        assertTrue(verificationToken.getExpiryDate().after(new Date()));
        assertNotEquals(notExpiredNotVerifiedToken.getToken(), verificationToken.getToken());
    }

    @Test
    void verifyEmail() {
        service.verifyEmail(notExpiredNotVerifiedToken.getToken());
        Mockito.verify(messageSender, Mockito.times(1)).sendEmailVerifiedMessage(notExpiredNotVerifiedToken.getEmail());
        VerificationToken verificationToken = repository.findByEmail(notExpiredNotVerifiedToken.getEmail()).get();
        assertTrue(verificationToken.isEmailVerified());
    }

    @Test
    void verifyEmailNotFoundToken() {
        assertThrows(NotFoundException.class, () -> service.verifyEmail(NOT_FOUND_TOKEN));
        Mockito.verify(messageSender, Mockito.times(0)).sendEmailVerifiedMessage(Mockito.anyString());
    }

    @Test
    void verifyEmailWhenTokenExpired() {
        assertThrows(EmailVerificationException.class, () -> service.verifyEmail(expiredToken.getToken()));
        Mockito.verify(messageSender, Mockito.times(0)).sendEmailVerifiedMessage(Mockito.anyString());
    }

    @Test
    void verifyEmailWhenAlreadyVerified() {
        assertThrows(EmailVerificationException.class, () -> service.verifyEmail(alreadyVerifiedToken.getToken()));
        Mockito.verify(messageSender, Mockito.times(0)).sendEmailVerifiedMessage(Mockito.anyString());
    }

    @Test
    void delete() {
        service.delete(alreadyVerifiedToken.getEmail());
        assertTrue(repository.findByEmail(alreadyVerifiedToken.getEmail()).isEmpty());
    }
}