package ru.javaprojects.energybalanceservice;


import org.springframework.test.web.servlet.ResultMatcher;
import ru.javaprojects.energybalanceservice.model.EnergyBalanceReport;

import static org.assertj.core.api.Assertions.assertThat;

public class EnergyBalanceReportMatcher {
    private EnergyBalanceReportMatcher() {
    }

    public static void assertMatch(EnergyBalanceReport actual, EnergyBalanceReport expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static ResultMatcher contentJson(EnergyBalanceReport expected) {
        return result -> assertMatch(TestUtil.readFromJsonMvcResult(result, EnergyBalanceReport.class), expected);
    }
}