package ru.javaprojects.mealservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.javaprojects.mealservice.web.json.JacksonObjectMapper;

@SpringBootApplication
public class MealServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MealServiceApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonObjectMapper.getMapper();
    }
}