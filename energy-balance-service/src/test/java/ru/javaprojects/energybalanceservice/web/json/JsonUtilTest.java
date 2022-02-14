package ru.javaprojects.energybalanceservice.web.json;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.javaprojects.energybalanceservice.model.EnergyBalanceReport;

import static ru.javaprojects.energybalanceservice.testdata.EnergyBalanceTestData.positiveReport;

class JsonUtilTest {

    @Test
    void readWriteValue() {
        String json = JsonUtil.writeValue(positiveReport);
        System.out.println(json);
        EnergyBalanceReport energyBalanceReport = JsonUtil.readValue(json, EnergyBalanceReport.class);
        Assertions.assertThat(energyBalanceReport).usingRecursiveComparison().isEqualTo(positiveReport);
    }
}