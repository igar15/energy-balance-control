package ru.javaprojects.passwordresetservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import ru.javaprojects.energybalancecontrolshared.web.json.JacksonObjectMapper;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtAuthorizationFilter;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtProvider;
import ru.javaprojects.energybalancecontrolshared.web.security.RestAccessDeniedHandler;
import ru.javaprojects.energybalancecontrolshared.web.security.RestAuthenticationEntryPoint;

@SpringBootApplication
@EnableDiscoveryClient
public class PasswordResetServiceApplication {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(PasswordResetServiceApplication.class, args);
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

    @Bean
    public Exchange eventExchange() {
        return new TopicExchange("ebcExchange");
    }

    @Bean
    public Queue userDeletedQueue() {
        return new Queue("passwordResetServiceUserDeletedQueue");
    }

    @Bean
    public Queue passwordResetQueue() {
        return new Queue("passwordResetQueue");
    }

    @Bean
    public Binding userDeletedBinding() {
        return BindingBuilder
                .bind(userDeletedQueue())
                .to(eventExchange())
                .with("user.deleted.message")
                .noargs();
    }

    @Bean
    public Binding passwordResetBinding() {
        return BindingBuilder
                .bind(passwordResetQueue())
                .to(eventExchange())
                .with("password.reset.message")
                .noargs();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper());
    }
}