package ru.javaprojects.gatewayserver.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtProvider;

import java.util.List;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtProvider jwtProvider;

    public AuthenticationManager(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getPrincipal().toString();
        try {
            String userId = jwtProvider.getSubject(token);
            if (jwtProvider.isTokenValid(userId, token)) {
                List<GrantedAuthority> authorities = jwtProvider.getAuthorities(token);
                return Mono.just(new UsernamePasswordAuthenticationToken(userId, null, authorities));
            }
        } catch (JWTVerificationException e) {
            return Mono.empty();
        }
        return Mono.empty();
    }
}