package ru.javaprojects.eurekaserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import ru.javaprojects.energybalancecontrolshared.web.json.JacksonObjectMapper;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtAuthorizationFilter;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtProvider;
import ru.javaprojects.energybalancecontrolshared.web.security.RestAccessDeniedHandler;
import ru.javaprojects.energybalancecontrolshared.web.security.RestAuthenticationEntryPoint;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonObjectMapper.getMapper();
    }

    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProvider(environment);
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(environment, jwtProvider());
    }

    @Bean
    public RestAccessDeniedHandler restAccessDeniedHandler() {
        return new RestAccessDeniedHandler();
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }
}