package ru.javaprojects.passwordresetservice.util;

import org.junit.jupiter.api.Test;
import ru.javaprojects.passwordresetservice.util.exception.PasswordResetException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.javaprojects.passwordresetservice.testdata.PasswordResetTokenTestData.expiredToken;
import static ru.javaprojects.passwordresetservice.testdata.PasswordResetTokenTestData.notExpiredToken;

class PasswordResetTokenUtilTest {

    @Test
    void checkTokenExpiredWhenExpired() {
        assertThrows(PasswordResetException.class, () -> PasswordResetTokenUtil.checkTokenExpired(expiredToken));
    }

    @Test
    void checkTokenExpiredWhenNotExpired() {
        assertDoesNotThrow(() -> PasswordResetTokenUtil.checkTokenExpired(notExpiredToken));
    }
}