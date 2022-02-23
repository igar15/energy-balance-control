package ru.javaprojects.gatewayserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtProvider;

@SpringBootApplication
@EnableEurekaClient
public class GatewayServerApplication {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }

    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProvider(environment);
    }
}