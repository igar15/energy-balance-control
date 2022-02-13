package ru.javaprojects.bxservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.javaprojects.bxservice.web.json.JacksonObjectMapper;

@SpringBootApplication
public class BxServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BxServiceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonObjectMapper.getMapper();
    }
}