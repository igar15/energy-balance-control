package ru.javaprojects.emailverificationservice.util;

import ru.javaprojects.emailverificationservice.model.VerificationToken;
import ru.javaprojects.emailverificationservice.util.exception.EmailVerificationException;

import java.util.Date;
import java.util.UUID;

public class VerificationTokenUtil {
    private VerificationTokenUtil() {
    }

    public static void checkTokenExpired(VerificationToken verificationToken) {
        Date expiryDate = verificationToken.getExpiryDate();
        if (new Date().after(expiryDate)) {
            throw new EmailVerificationException("Verification token for " + verificationToken.getEmail() + " expired");
        }
    }

    public static void checkAlreadyVerified(VerificationToken verificationToken) {
        if (verificationToken.isEmailVerified()) {
            throw new EmailVerificationException("Email already verified:" + verificationToken.getEmail());
        }
    }

    public static VerificationToken prepareToken(VerificationToken verificationToken, String email, Date expiryDate) {
        String token = UUID.randomUUID().toString();
        verificationToken.setEmail(email);
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(expiryDate);
        return verificationToken;
    }
}