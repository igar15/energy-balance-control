package ru.javaprojects.passwordresetservice.testdata;

import ru.javaprojects.passwordresetservice.model.PasswordResetToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PasswordResetTokenTestData {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd Hh:mm:ss");

    public static final PasswordResetToken expiredToken = new PasswordResetToken(100002L, "user3@test.com", "52bde839-9779-4005-b81c-9131c9590d79", parseDate("2022-02-06 19:35:56"));
    public static final PasswordResetToken notExpiredToken = new PasswordResetToken(100000L, "user1@test.com", "b5fc98f1-40ec-485e-b316-8453f560bd78", parseDate("2052-02-05 12:10:00"));

    public static final String NOT_FOUND_TOKEN = "fsdfdsfds-aca0-4348-9a7e-fsdfdsf";

    public static final String USER_EMAIL = "john@test.com";

    public static final String NEW_PASSWORD = "newPassword";

    public static final String TOKEN_PARAM = "token";
    public static final String PASSWORD_PARAM = "password";

    public static final String ACTUATOR_PATH = "/actuator/beans";

    private static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}