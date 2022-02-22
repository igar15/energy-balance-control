package ru.javaprojects.bxservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.javaprojects.bxservice.testdata.BasicExchangeTestData.*;

class BasicExchangeUtilTest {

    @Test
    void calculateBxCalories() {
        assertEquals(USER_BX_CALORIES, BasicExchangeUtil.calculateBxCalories(userBxDetails));
        assertEquals(ADMIN_BX_CALORIES, BasicExchangeUtil.calculateBxCalories(adminBxDetails));
    }
}