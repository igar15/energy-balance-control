package ru.javaprojects.emailverificationservice.testdata;

import ru.javaprojects.emailverificationservice.model.VerificationToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VerificationTokenTestData {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd Hh:mm:ss");

    public static final VerificationToken expiredToken = new VerificationToken(100002L, "user3@test.com", "a8977264-d5dd-4fg6-8n66-8c1f7a1ccc99", parseDate("2022-02-06 10:15:12"), false);
    public static final VerificationToken notExpiredNotVerifiedToken = new VerificationToken(100000L, "user1@test.com", "04c478df-aca0-4348-9a7e-4823435c6c11", parseDate("2052-02-05 10:00:00"), false);
    public static final VerificationToken alreadyVerifiedToken = new VerificationToken(100001L, "user2@test.com", "a6480258-c4dc-4f43-9a58-4c1f7a1dda06", parseDate("2052-02-07 18:15:24"), true);

    public static final String NOT_FOUND_TOKEN = "fsdfdsfds-aca0-4348-9a7e-fsdfdsf";

    public static final String NEW_USER_EMAIL = "john@test.com";

    public static final String TOKEN_PARAM = "token";

    private static Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}