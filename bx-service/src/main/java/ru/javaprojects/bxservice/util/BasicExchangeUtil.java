package ru.javaprojects.bxservice.util;

import ru.javaprojects.bxservice.to.UserDetails;

import static ru.javaprojects.bxservice.to.UserDetails.Sex.MAN;

public class BasicExchangeUtil {
    private BasicExchangeUtil() {
    }

    public static int calculateBxCalories(UserDetails userDetails) {
        double sameData = 10 * userDetails.getWeight() + 6.25 * userDetails.getGrowth() - 5 * userDetails.getAge();
        return (int) Math.round((userDetails.getSex() == MAN) ? sameData + 5 : sameData - 161);
    }
}