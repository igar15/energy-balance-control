package ru.javaprojects.emailverificationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.javaprojects.energybalancecontrolshared.web.json.JacksonObjectMapper;

@SpringBootApplication
public class EmailVerificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailVerificationServiceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonObjectMapper.getMapper();
    }
}