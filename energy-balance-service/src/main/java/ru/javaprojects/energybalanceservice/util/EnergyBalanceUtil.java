package ru.javaprojects.energybalanceservice.util;

import ru.javaprojects.energybalanceservice.model.EnergyBalanceReport;

import java.time.LocalDate;

public class EnergyBalanceUtil {
    public static final String POSITIVE_BALANCE_MESSAGE = "Positive energy balance. You gained weight";
    public static final String NEGATIVE_BALANCE_MESSAGE = "Negative energy balance. You lost weight";
    public static final String ZERO_BALANCE_MESSAGE = "Zero energy balance. Your weight has not changed";
    public static final String ERROR_MESSAGE = "Failed to calculate the energy balance. Not enough data";

    private EnergyBalanceUtil() {
    }

    public static EnergyBalanceReport makeReport(LocalDate date, Integer mealCalories, Integer trainingCalories, Integer bxCalories) {
        Integer energyBalanceValue = null;
        String message = ERROR_MESSAGE;
        if (isCaloriesValid(mealCalories, trainingCalories, bxCalories)) {
            energyBalanceValue = calculateEnergyBalanceValue(mealCalories, trainingCalories, bxCalories);
            if (energyBalanceValue < 0) {
                message = NEGATIVE_BALANCE_MESSAGE;
            } else if (energyBalanceValue > 0) {
                message = POSITIVE_BALANCE_MESSAGE;
            } else {
                message = ZERO_BALANCE_MESSAGE;
            }
        }
        return new EnergyBalanceReport(date, mealCalories, trainingCalories, bxCalories, energyBalanceValue, message);
    }

    static int calculateEnergyBalanceValue(int mealCalories, int trainingCalories, int bxCalories) {
        return mealCalories - trainingCalories - bxCalories;
    }

    static boolean isCaloriesValid(Integer mealCalories, Integer trainingCalories, Integer bxCalories) {
        return (mealCalories != null && mealCalories >= 0) &&
                (trainingCalories != null && trainingCalories >= 0) &&
                (bxCalories != null && bxCalories >= 0);
    }
}