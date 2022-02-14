package ru.javaprojects.emailverificationservice.service;

import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.emailverificationservice.model.VerificationToken;
import ru.javaprojects.emailverificationservice.repository.VerificationTokenRepository;

import java.util.Date;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private final VerificationTokenRepository repository;
    private final Environment environment;
    private JavaMailSender mailSender;

    public EmailVerificationService(VerificationTokenRepository repository, Environment environment, JavaMailSender mailSender) {
        this.repository = repository;
        this.environment = environment;
        this.mailSender = mailSender;
    }

    @Transactional
    public void sendVerificationEmail(String email) {
        String token = UUID.randomUUID().toString();
        Date expiryDate = new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("email.verification-token.expiration-time")));
        VerificationToken verificationToken = repository.findByEmail(email).orElse(new VerificationToken());
        verificationToken.setEmail(email);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(expiryDate);
        repository.save(verificationToken);
        sendEmail(email, token);
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
}