package ru.javaprojects.bxservice.util;

import org.junit.jupiter.api.Test;
import ru.javaprojects.bxservice.to.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.bxservice.testdata.BasicExchangeTestData.*;
import static ru.javaprojects.bxservice.to.UserDetails.Sex.MAN;
import static ru.javaprojects.bxservice.to.UserDetails.Sex.WOMAN;

class BasicExchangeUtilTest {

    @Test
    void calculateBxCalories() {
        int i = BasicExchangeUtil.calculateBxCalories(updatedUser1Details);
        assertEquals(USER1_BX_CALORIES, BasicExchangeUtil.calculateBxCalories(user1Details));
        assertEquals(USER2_BX_CALORIES, BasicExchangeUtil.calculateBxCalories(user2Details));
    }
}