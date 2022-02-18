package ru.javaprojects.energybalanceservice.web.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.energybalanceservice.web.controller.AbstractControllerTest;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.energybalanceservice.testdata.EnergyBalanceTestData.DATE;
import static ru.javaprojects.energybalanceservice.testdata.EnergyBalanceTestData.DATE_PARAM;
import static ru.javaprojects.energybalanceservice.testdata.UserTestData.USER1_ID_STRING;
import static ru.javaprojects.energybalanceservice.util.exception.ErrorType.BAD_TOKEN_ERROR;
import static ru.javaprojects.energybalanceservice.web.AppExceptionHandler.EXCEPTION_BAD_TOKEN;
import static ru.javaprojects.energybalanceservice.web.security.JwtProvider.AUTHORITIES;

class JwtAuthorizationFilterTest extends AbstractControllerTest {

    @Autowired
    private Environment environment;

    private HttpHeaders jwtHeader;
    private HttpHeaders jwtExpiredHeader;
    private HttpHeaders jwtInvalidHeader;

    @PostConstruct
    private void postConstruct() {
        String secretKey = environment.getProperty("jwt.secretKey");
        jwtHeader = getHeaders(generateCustomAuthorizationToken(USER1_ID_STRING,
                new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("authorization.token.expiration-time"))),
                secretKey, "ROLE_USER"));
        jwtExpiredHeader = getHeaders(generateCustomAuthorizationToken(USER1_ID_STRING,
                new Date(System.currentTimeMillis() - 10000), secretKey, "ROLE_USER"));
        jwtInvalidHeader = getHeaders(generateCustomAuthorizationToken(USER1_ID_STRING,
                new Date(System.currentTimeMillis() + Long.parseLong(environment.getProperty("authorization.token.expiration-time"))),
                UUID.randomUUID().toString(), "ROLE_USER"));
    }

    @Test
    void getReport() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(DATE_PARAM, DATE.toString())
                .headers(jwtHeader))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getReportTokenExpired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(DATE_PARAM, DATE.toString())
                .headers(jwtExpiredHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(BAD_TOKEN_ERROR))
                .andExpect(detailMessage(EXCEPTION_BAD_TOKEN));
    }

    @Test
    void getReportTokenInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param(DATE_PARAM, DATE.toString())
                .headers(jwtInvalidHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(BAD_TOKEN_ERROR))
                .andExpect(detailMessage(EXCEPTION_BAD_TOKEN));
    }

    private HttpHeaders getHeaders(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, environment.getProperty("authorization.token.header.prefix") + token);
        return httpHeaders;
    }

    private String generateCustomAuthorizationToken(String userId, Date expirationDate, String secretKey, String ... authorities) {
        return JWT.create()
                .withIssuer(environment.getProperty("authorization.token.issuer"))
                .withAudience(environment.getProperty("authorization.token.audience"))
                .withIssuedAt(new Date())
                .withSubject(userId)
                .withArrayClaim(AUTHORITIES, authorities)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC512(secretKey));
    }
}