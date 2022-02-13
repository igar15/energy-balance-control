package ru.javaprojects.bxservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.javaprojects.bxservice.testdata.BasicExchangeTestData.*;

class BasicExchangeUtilTest {

    @Test
    void calculateBxCalories() {
        assertEquals(USER1_BX_CALORIES, BasicExchangeUtil.calculateBxCalories(user1Details));
        assertEquals(USER2_BX_CALORIES, BasicExchangeUtil.calculateBxCalories(user2Details));
    }
}