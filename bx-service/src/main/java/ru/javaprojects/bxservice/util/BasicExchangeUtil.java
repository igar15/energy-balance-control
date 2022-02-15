package ru.javaprojects.bxservice.util;

import ru.javaprojects.bxservice.to.UserBxDetails;

import static ru.javaprojects.bxservice.to.UserBxDetails.Sex.MAN;

public class BasicExchangeUtil {
    private BasicExchangeUtil() {
    }

    public static int calculateBxCalories(UserBxDetails userBxDetails) {
        double sameData = 10 * userBxDetails.getWeight() + 6.25 * userBxDetails.getGrowth() - 5 * userBxDetails.getAge();
        return (int) Math.round((userBxDetails.getSex() == MAN) ? sameData + 5 : sameData - 161);
    }
}