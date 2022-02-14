package ru.javaprojects.energybalanceservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.javaprojects.energybalanceservice.web.json.JacksonObjectMapper;

@SpringBootApplication
public class EnergyBalanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyBalanceServiceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonObjectMapper.getMapper();
    }
}