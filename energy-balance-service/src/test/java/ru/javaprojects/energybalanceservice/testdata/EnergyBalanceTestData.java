package ru.javaprojects.energybalanceservice.testdata;

import ru.javaprojects.energybalancecontrolshared.test.TestMatcher;
import ru.javaprojects.energybalanceservice.model.EnergyBalanceReport;

import java.time.LocalDate;

import static java.time.Month.FEBRUARY;
import static ru.javaprojects.energybalanceservice.util.EnergyBalanceUtil.*;

public class EnergyBalanceTestData {
    public static final TestMatcher<EnergyBalanceReport> ENERGY_BALANCE_REPORT_MATCHER = TestMatcher.usingIgnoringFieldsComparator(EnergyBalanceReport.class);

    public static final Integer MEAL_CALORIES = 2100;
    public static final Integer TRAINING_CALORIES = 190;
    public static final Integer BX_CALORIES = 1891;

    public static final LocalDate DATE = LocalDate.of(2022, FEBRUARY, 6);

    public static final String DATE_PARAM = "date";

    public static final EnergyBalanceReport positiveReport = new EnergyBalanceReport(DATE, MEAL_CALORIES, TRAINING_CALORIES, BX_CALORIES, 19, POSITIVE_BALANCE_MESSAGE);
    public static final EnergyBalanceReport negativeReport = new EnergyBalanceReport(DATE, MEAL_CALORIES, TRAINING_CALORIES + 30, BX_CALORIES, -11, NEGATIVE_BALANCE_MESSAGE);
    public static final EnergyBalanceReport zeroReport = new EnergyBalanceReport(DATE, MEAL_CALORIES, TRAINING_CALORIES + 19, BX_CALORIES, 0, ZERO_BALANCE_MESSAGE);
    public static final EnergyBalanceReport errorReport = new EnergyBalanceReport(DATE, -1, TRAINING_CALORIES, BX_CALORIES, null, ERROR_MESSAGE);
}