package ru.javaprojects.emailverificationservice.service;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.emailverificationservice.messaging.MessageSender;
import ru.javaprojects.emailverificationservice.model.VerificationToken;
import ru.javaprojects.emailverificationservice.repository.VerificationTokenRepository;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;

import java.util.Date;

import static ru.javaprojects.emailverificationservice.util.VerificationTokenUtil.*;

@Service
public class EmailVerificationService {
    private static final String MUST_NOT_BE_NULL = " must not be null";
    private VerificationTokenRepository repository;
    private Environment environment;
    private JavaMailSender mailSender;
    private MessageSender messageSender;

    public EmailVerificationService(VerificationTokenRepository repository, Environment environment,
                                    JavaMailSender mailSender, MessageSender messageSender) {
        this.repository = repository;
        this.environment = environment;
        this.mailSender = mailSender;
        this.messageSender = messageSender;
    }

    @Transactional
    public void sendVerificationEmail(String email) {
        Assert.notNull(email, "email" + MUST_NOT_BE_NULL);
        VerificationToken verificationToken = repository.findByEmail(email).orElse(new VerificationToken());
        checkAlreadyVerified(verificationToken);
        prepareToken(verificationToken, email, getExpiryDate());
        repository.save(verificationToken);
        sendEmail(email, verificationToken.getToken());
    }

    @Transactional
    public void verifyEmail(String token) {
        Assert.notNull(token, "token" + MUST_NOT_BE_NULL);
        VerificationToken verificationToken = repository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Not found verification record with token=" + token));
        checkAlreadyVerified(verificationToken);
        checkTokenExpired(verificationToken);
        verificationToken.setEmailVerified(true);
        messageSender.sendEmailConfirmedMessage(verificationToken.getEmail());
    }

    public void delete(String email) {
        Assert.notNull(email, "email" + MUST_NOT_BE_NULL);
        repository.deleteByEmail(email);
    }

    private Date getExpiryDate() {
        return new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("email.verification-token.expiration-time")));
    }

    private void sendEmail(String email, String token) {
        String url = String.format("%s?token=%s", environment.getProperty("email.verification-token.url"), token);
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Email Verification");
        mail.setText("Please open the following URL to verify your email: \r\n" + url);
        mail.setFrom(environment.getProperty("support.email"));
        mailSender.send(mail);
    }

    //use only for tests
    void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}