package ru.javaprojects.energybalanceservice.web.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import ru.javaprojects.energybalancecontrolshared.web.security.BasicWebSecurity;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtAuthorizationFilter;
import ru.javaprojects.energybalancecontrolshared.web.security.RestAccessDeniedHandler;
import ru.javaprojects.energybalancecontrolshared.web.security.RestAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurity extends BasicWebSecurity {
    public WebSecurity(JwtAuthorizationFilter jwtAuthorizationFilter,
                       RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                       RestAccessDeniedHandler restAccessDeniedHandler) {
        super(jwtAuthorizationFilter, restAuthenticationEntryPoint, restAccessDeniedHandler);
    }
}