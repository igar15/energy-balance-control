package ru.javaprojects.emailverificationservice.util;

import org.junit.jupiter.api.Test;
import ru.javaprojects.emailverificationservice.util.exception.EmailVerificationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.emailverificationservice.testdata.VerificationTokenTestData.*;

class VerificationTokenUtilTest {

    @Test
    void checkTokenExpiredWhenExpired() {
        assertThrows(EmailVerificationException.class, () -> VerificationTokenUtil.checkTokenExpired(expiredToken));
    }

    @Test
    void checkTokenExpiredWhenNotExpired() {
        assertDoesNotThrow(() -> VerificationTokenUtil.checkTokenExpired(notExpiredNotVerifiedToken));
    }

    @Test
    void checkAlreadyVerifiedWhenVerified() {
        assertThrows(EmailVerificationException.class, () -> VerificationTokenUtil.checkAlreadyVerified(alreadyVerifiedToken));
    }

    @Test
    void prepareVerificationTokenWhenNotVerified() {
        assertDoesNotThrow(() -> VerificationTokenUtil.checkTokenExpired(notExpiredNotVerifiedToken));
    }
}