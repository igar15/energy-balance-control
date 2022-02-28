package ru.javaprojects.emailverificationservice;

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
public class EmailVerificationServiceApplication {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(EmailVerificationServiceApplication.class, args);
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
        return new Queue("emailVerificationServiceUserDeletedQueue");
    }

    @Bean
    public Queue emailVerifyQueue() {
        return new Queue("emailVerifyQueue");
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
    public Binding emailVerifyBinding() {
        return BindingBuilder
                .bind(emailVerifyQueue())
                .to(eventExchange())
                .with("email.verify.message")
                .noargs();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper());
    }
}