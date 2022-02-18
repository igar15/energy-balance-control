package ru.javaprojects.passwordresetservice.util;

import ru.javaprojects.passwordresetservice.model.PasswordResetToken;
import ru.javaprojects.passwordresetservice.util.exception.PasswordResetException;

import java.util.Date;
import java.util.UUID;

public class PasswordResetTokenUtil {
    private PasswordResetTokenUtil() {
    }

    public static void checkTokenExpired(PasswordResetToken passwordResetToken) {
        Date expiryDate = passwordResetToken.getExpiryDate();
        if (new Date().after(expiryDate)) {
            throw new PasswordResetException("Password Reset token for " + passwordResetToken.getEmail() + " expired");
        }
    }

    public static PasswordResetToken prepareToken(PasswordResetToken passwordResetToken, String email, Date expiryDate) {
        String token = UUID.randomUUID().toString();
        passwordResetToken.setEmail(email);
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(expiryDate);
        return passwordResetToken;
    }
}