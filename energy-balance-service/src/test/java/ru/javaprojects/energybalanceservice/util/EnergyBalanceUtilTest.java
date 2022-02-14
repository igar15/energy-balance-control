package ru.javaprojects.energybalanceservice.util;

import org.junit.jupiter.api.Test;
import ru.javaprojects.energybalanceservice.model.EnergyBalanceReport;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.energybalanceservice.testdata.EnergyBalanceTestData.*;

class EnergyBalanceUtilTest {

    @Test
    void calculateEnergyBalanceValue() {
        assertEquals(150, EnergyBalanceUtil.calculateEnergyBalanceValue(2100, 50, 1900));
        assertEquals(-100, EnergyBalanceUtil.calculateEnergyBalanceValue(1800, 0, 1900));
    }

    @Test
    void checkCaloriesValid() {
        assertTrue(EnergyBalanceUtil.isCaloriesValid(2000, 100, 1900));
        assertTrue(EnergyBalanceUtil.isCaloriesValid(0, 0, 0));
    }

    @Test
    void checkCaloriesInvalid() {
        assertFalse(EnergyBalanceUtil.isCaloriesValid(null, 100, 1900));
        assertFalse(EnergyBalanceUtil.isCaloriesValid(2000, null, 1900));
        assertFalse(EnergyBalanceUtil.isCaloriesValid(2000, 100, null));
        assertFalse(EnergyBalanceUtil.isCaloriesValid(-1, 100, 1900));
        assertFalse(EnergyBalanceUtil.isCaloriesValid(2000, -1, 1900));
        assertFalse(EnergyBalanceUtil.isCaloriesValid(2000, 100, -1));
    }

    @Test
    void makeReport() {
        EnergyBalanceReport created = EnergyBalanceUtil.makeReport(DATE, MEAL_CALORIES, TRAINING_CALORIES, BX_CALORIES);
        assertEquals(positiveReport, created);
        created = EnergyBalanceUtil.makeReport(DATE, MEAL_CALORIES, TRAINING_CALORIES + 30, BX_CALORIES);
        assertEquals(negativeReport, created);
        created = EnergyBalanceUtil.makeReport(DATE, MEAL_CALORIES, TRAINING_CALORIES + 19, BX_CALORIES);
        assertEquals(zeroReport, created);
    }

    @Test
    void makeErrorReport() {
        EnergyBalanceReport created = EnergyBalanceUtil.makeReport(DATE, -1, TRAINING_CALORIES, BX_CALORIES);
        assertEquals(errorReport, created);
    }
}