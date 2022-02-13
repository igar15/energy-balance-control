package ru.javaprojects.bxservice.testdata;

import ru.javaprojects.bxservice.model.BasicExchange;
import ru.javaprojects.bxservice.to.UserDetails;

import java.time.LocalDate;

import static java.time.LocalDate.of;
import static java.time.Month.FEBRUARY;
import static ru.javaprojects.bxservice.model.BasicExchange.START_SEQ;
import static ru.javaprojects.bxservice.to.UserDetails.Sex.MAN;
import static ru.javaprojects.bxservice.to.UserDetails.Sex.WOMAN;

public class BasicExchangeTestData {
    public static final long BASIC_EXCHANGE1_ID = START_SEQ;
    public static final long USER2_BASIC_EXCHANGE1_ID = START_SEQ + 3;
    public static final long NOT_FOUND = 10;
    public static final long USER1_ID = 200000;
    public static final long USER2_ID = 200001;
    public static final String USER1_ID_STRING = "200000";
    public static final String USER2_ID_STRING = "200001";

    public static final LocalDate FEBRUARY_5_2022 = of(2022, FEBRUARY, 5);
    public static final LocalDate FEBRUARY_6_2022 = of(2022, FEBRUARY, 6);
    public static final LocalDate FEBRUARY_7_2022 = of(2022, FEBRUARY, 7);

    public static final int USER1_BX_CALORIES = 1891;
    public static final int UPDATED_USER1_BX_CALORIES = 1866;
    public static final int USER2_BX_CALORIES = 1308;

    public static final BasicExchange basicExchange1 = new BasicExchange(BASIC_EXCHANGE1_ID, FEBRUARY_5_2022, USER1_BX_CALORIES);
    public static final BasicExchange basicExchange2 = new BasicExchange(BASIC_EXCHANGE1_ID + 1, FEBRUARY_6_2022, USER1_BX_CALORIES);
    public static final BasicExchange basicExchange3 = new BasicExchange(BASIC_EXCHANGE1_ID + 2, FEBRUARY_7_2022, USER1_BX_CALORIES);

    public static final UserDetails user1Details = new UserDetails(MAN, 90, 185, 34);
    public static final UserDetails updatedUser1Details = new UserDetails(MAN, 88, 185, 35);
    public static final UserDetails user2Details = new UserDetails(WOMAN, 60, 163, 30);
}