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
import static ru.javaprojects.energybalanceservice.testdata.EnergyBalanceTestData.USER1_ID_STRING;
import static ru.javaprojects.energybalanceservice.util.exception.ErrorType.BAD_TOKEN_ERROR;
import static ru.javaprojects.energybalanceservice.web.AppExceptionHandler.EXCEPTION_BAD_TOKEN;
import static ru.javaprojects.energybalanceservice.web.security.JwtProvider.*;

class JwtAuthorizationFilterTest extends AbstractControllerTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private Environment environment;

    private HttpHeaders jwtHeader;

    private HttpHeaders jwtExpiredHeader;

    private HttpHeaders jwtInvalidHeader;

    @PostConstruct
    private void postConstruct() {
        String secretKey = environment.getProperty("jwt.secretKey");
        jwtHeader = getHeaders(jwtProvider.generateAuthorizationToken(USER1_ID_STRING, "ROLE_USER"));
        jwtExpiredHeader = getHeaders(generateCustomAuthorizationToken(USER1_ID_STRING, (new Date(System.currentTimeMillis() - 10000)), secretKey));
        jwtInvalidHeader = getHeaders(generateCustomAuthorizationToken(USER1_ID_STRING, (new Date(System.currentTimeMillis() + AUTHORIZATION_TOKEN_EXPIRATION_TIME)), UUID.randomUUID().toString()));
    }

    @Test
    void getReport() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", DATE.toString())
                .headers(jwtHeader))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getReportTokenExpired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", DATE.toString())
                .headers(jwtExpiredHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(BAD_TOKEN_ERROR))
                .andExpect(detailMessage(EXCEPTION_BAD_TOKEN));
    }

    @Test
    void getReportTokenInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", DATE.toString())
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

    private String generateCustomAuthorizationToken(String userId, Date expirationDate, String secretKey) {
        return JWT.create()
                .withIssuer(JAVA_PROJECTS)
                .withAudience(ENERGY_BALANCE_CONTROL_AUDIENCE)
                .withIssuedAt(new Date())
                .withSubject(userId)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC512(secretKey));
    }
}