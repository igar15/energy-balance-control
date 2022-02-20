package ru.javaprojects.passwordresetservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import ru.javaprojects.energybalancecontrolshared.web.json.JacksonObjectMapper;

@SpringBootApplication
@EnableDiscoveryClient
public class PasswordResetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PasswordResetServiceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonObjectMapper.getMapper();
    }
}