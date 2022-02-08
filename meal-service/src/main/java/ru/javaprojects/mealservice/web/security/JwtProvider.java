package ru.javaprojects.mealservice.web.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtProvider {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_TOKEN_HEADER = "Authorization-Token";
    public static final String JAVA_PROJECTS = "javaprojects.ru";
    public static final String REWARD_CALCULATOR_AUDIENCE = "Reward Calculator System";
    public static final long AUTHORIZATION_TOKEN_EXPIRATION_TIME = 432_000_000; // 5 days
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";

    private final Environment environment;

    public JwtProvider(Environment environment) {
        this.environment = environment;
    }

    public  String generateAuthorizationToken(AuthorizedUser authorizedUser) {
        String[] claims = getClaimsFromUser(authorizedUser);
        return JWT.create()
                .withIssuer(JAVA_PROJECTS)
                .withAudience(REWARD_CALCULATOR_AUDIENCE)
                .withIssuedAt(new Date())
                .withSubject(String.valueOf(authorizedUser.getId()))
                .withArrayClaim("authorities", claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + AUTHORIZATION_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(Objects.requireNonNull(environment.getProperty("jwt.secretKey"))));
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(String userName, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userName, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    public String getSubject(String token) {
        JWTVerifier jwtVerifier = getJWTVerifier();
        return jwtVerifier.verify(token).getSubject();
    }

    public boolean isTokenValid(String subject, String token) {
        return !subject.isEmpty() && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        JWTVerifier jwtVerifier = getJWTVerifier();
        Date expirationDate = jwtVerifier.verify(token).getExpiresAt();
        return expirationDate.before(new Date());
    }

    private String[] getClaimsFromUser(AuthorizedUser authorizedUser) {
        return authorizedUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim("authorities").asArray(String.class);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier = null;
        try {
            verifier = JWT.require(Algorithm.HMAC512(Objects.requireNonNull(environment.getProperty("jwt.secretKey")))).build();
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }
}