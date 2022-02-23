package ru.javaprojects.gatewayserver.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurity {

    @Value("${gateway-server.free-paths}")
    private String[] freePaths;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public WebSecurity(AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    // TODO ADD CORS
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                .pathMatchers(freePaths).permitAll()
                .pathMatchers("/actuator/*").hasRole("ADMIN")
                .anyExchange().authenticated()
                .and().exceptionHandling()
                .authenticationEntryPoint(((exchange, e) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(UNAUTHORIZED))))
                .accessDeniedHandler(((exchange, e) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(FORBIDDEN))))
                .and().build();
    }
}