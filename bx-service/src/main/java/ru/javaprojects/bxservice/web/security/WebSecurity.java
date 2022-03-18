package ru.javaprojects.bxservice.web.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtAuthorizationFilter;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtWebSecurity;
import ru.javaprojects.energybalancecontrolshared.web.security.RestAccessDeniedHandler;
import ru.javaprojects.energybalancecontrolshared.web.security.RestAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurity extends JwtWebSecurity {

    public WebSecurity(JwtAuthorizationFilter jwtAuthorizationFilter,
                       RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                       RestAccessDeniedHandler restAccessDeniedHandler) {
        super(jwtAuthorizationFilter, restAuthenticationEntryPoint, restAccessDeniedHandler);
    }

    @Override
    protected void configureAuthorizeRequests(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/actuator/*").access("hasRole('ADMIN') and hasIpAddress(@environment.getProperty('gateway-server.ip'))")
                .anyRequest().access("isAuthenticated() and hasIpAddress(@environment.getProperty('gateway-server.ip'))");
    }
}