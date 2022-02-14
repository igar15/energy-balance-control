package ru.javaprojects.energybalanceservice.testdata;

import ru.javaprojects.energybalanceservice.model.EnergyBalanceReport;
import ru.javaprojects.energybalanceservice.util.EnergyBalanceUtil;

import java.time.LocalDate;
import java.time.Month;

import static java.time.Month.FEBRUARY;
import static ru.javaprojects.energybalanceservice.util.EnergyBalanceUtil.*;

public class EnergyBalanceTestData {
    public static final String USER1_ID_STRING = "200000";

    public static final Integer MEAL_CALORIES = 2100;
    public static final Integer TRAINING_CALORIES = 190;
    public static final Integer BX_CALORIES = 1891;

    public static final LocalDate DATE = LocalDate.of(2022, FEBRUARY, 6);

    public static final EnergyBalanceReport positiveReport = new EnergyBalanceReport(DATE, MEAL_CALORIES, TRAINING_CALORIES, BX_CALORIES, 19, POSITIVE_BALANCE_MESSAGE);
    public static final EnergyBalanceReport negativeReport = new EnergyBalanceReport(DATE, MEAL_CALORIES, TRAINING_CALORIES + 30, BX_CALORIES, -11, NEGATIVE_BALANCE_MESSAGE);
    public static final EnergyBalanceReport zeroReport = new EnergyBalanceReport(DATE, MEAL_CALORIES, TRAINING_CALORIES + 19, BX_CALORIES, 0, ZERO_BALANCE_MESSAGE);
    public static final EnergyBalanceReport errorReport = new EnergyBalanceReport(DATE, -1, TRAINING_CALORIES, BX_CALORIES, null, ERROR_MESSAGE);
}