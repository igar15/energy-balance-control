package ru.javaprojects.passwordresetservice.service;

import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.passwordresetservice.messaging.MessageSender;
import ru.javaprojects.passwordresetservice.model.PasswordResetToken;
import ru.javaprojects.passwordresetservice.repository.PasswordResetTokenRepository;
import ru.javaprojects.passwordresetservice.util.exception.PasswordResetException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

import static ru.javaprojects.passwordresetservice.util.PasswordResetTokenUtil.checkTokenExpired;
import static ru.javaprojects.passwordresetservice.util.PasswordResetTokenUtil.prepareToken;

@Service
public class PasswordResetService {
    private static final String MUST_NOT_BE_NULL = " must not be null";
    private PasswordResetTokenRepository repository;
    private Environment environment;
    private JavaMailSender mailSender;
    private MessageSender messageSender;

    public PasswordResetService(PasswordResetTokenRepository repository, Environment environment,
                                JavaMailSender mailSender, MessageSender messageSender) {
        this.repository = repository;
        this.environment = environment;
        this.mailSender = mailSender;
        this.messageSender = messageSender;
    }

    @Transactional
    public void sendPasswordResetEmail(String email) {
        Assert.notNull(email, "email" + MUST_NOT_BE_NULL);
        PasswordResetToken passwordResetToken = repository.findByEmail(email).orElse(new PasswordResetToken());
        prepareToken(passwordResetToken, email, getExpiryDate());
        repository.save(passwordResetToken);
        sendEmail(email, passwordResetToken.getToken());
    }

    public PasswordResetToken checkToken(String token) {
        Assert.notNull(token, "token" + MUST_NOT_BE_NULL);
        PasswordResetToken passwordResetToken = repository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Not found password reset record with token=" + token));
        checkTokenExpired(passwordResetToken);
        return passwordResetToken;
    }

    public void resetPassword(String token, String password) {
        Assert.notNull(password, "password" + MUST_NOT_BE_NULL);
        PasswordResetToken passwordResetToken = checkToken(token);
        repository.delete(passwordResetToken);
        messageSender.sendPasswordChangedMessage(passwordResetToken.getEmail(), password);
    }

    public void delete(String email) {
        Assert.notNull(email, "email" + MUST_NOT_BE_NULL);
        repository.deleteByEmail(email);
    }

    private Date getExpiryDate() {
        return new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("email.password-reset-token.expiration-time")));
    }

    private void sendEmail(String email, String token) {
        String url = String.format("<a href='%s?token=%s'>Password Reset Link</a>", environment.getProperty("email.password-reset-token.url"), token);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, "utf-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setFrom(environment.getProperty("support.email"));
            mimeMessageHelper.setSubject("EBC System: Password Reset Message");
            mimeMessageHelper.setText("<p>Please follow the link to reset your password:</p>" + url, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new PasswordResetException(e.getMessage());
        }
    }

    //use only for tests
    void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}