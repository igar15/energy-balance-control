package ru.javaprojects.userservice.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtAuthorizationFilter;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtWebSecurity;
import ru.javaprojects.energybalancecontrolshared.web.security.RestAccessDeniedHandler;
import ru.javaprojects.energybalancecontrolshared.web.security.RestAuthenticationEntryPoint;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class WebSecurity extends JwtWebSecurity {
    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    public WebSecurity(JwtAuthorizationFilter jwtAuthorizationFilter, RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                       RestAccessDeniedHandler restAccessDeniedHandler, UserService service, PasswordEncoder passwordEncoder) {
        super(jwtAuthorizationFilter, restAuthenticationEntryPoint, restAccessDeniedHandler);
        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            try {
                User user = service.getByEmail(email.toLowerCase());
                return new AuthorizedUser(user);
            } catch (NotFoundException e) {
                throw new UsernameNotFoundException("User '" + email + "' was not found");
            }
        };
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configureAuthorizeRequests(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/profile/login").access("permitAll() and hasIpAddress(@environment.getProperty('gateway-server.ip'))")
                .antMatchers("/api/profile/register").access("isAnonymous() and hasIpAddress(@environment.getProperty('gateway-server.ip'))")
                .antMatchers("/api/profile/password/reset").access("isAnonymous() and hasIpAddress(@environment.getProperty('gateway-server.ip'))")
                .antMatchers("/api/profile/email/verify").access("isAnonymous() and hasIpAddress(@environment.getProperty('gateway-server.ip'))")
                .antMatchers("/actuator/*").hasRole("ADMIN")
                .anyRequest().access("isAuthenticated() and hasIpAddress(@environment.getProperty('gateway-server.ip'))");
    }
}