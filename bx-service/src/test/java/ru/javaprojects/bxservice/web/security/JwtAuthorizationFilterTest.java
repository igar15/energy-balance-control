package ru.javaprojects.bxservice.web.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.bxservice.util.exception.ErrorType;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javaprojects.bxservice.testdata.BasicExchangeTestData.*;
import static ru.javaprojects.bxservice.util.exception.ErrorType.BAD_TOKEN_ERROR;
import static ru.javaprojects.bxservice.web.AppExceptionHandler.EXCEPTION_BAD_TOKEN;
import static ru.javaprojects.bxservice.web.security.JwtProvider.*;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
class JwtAuthorizationFilterTest {
    private static final String REST_URL = "/api/bx";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private Environment environment;

    private HttpHeaders jwtHeader;

    private HttpHeaders jwtExpiredHeader;

    private HttpHeaders jwtInvalidHeader;

    public ResultMatcher errorType(ErrorType type) {
        return jsonPath("$.type").value(type.name());
    }

    public ResultMatcher detailMessage(String code) {
        return jsonPath("$.details").value(code);
    }

    @PostConstruct
    private void postConstruct() {
        String secretKey = environment.getProperty("jwt.secretKey");
        jwtHeader = getHeaders(jwtProvider.generateAuthorizationToken(USER1_ID_STRING, "ROLE_USER"));
        jwtExpiredHeader = getHeaders(generateCustomAuthorizationToken(USER1_ID_STRING, (new Date(System.currentTimeMillis() - 10000)), secretKey));
        jwtInvalidHeader = getHeaders(generateCustomAuthorizationToken(USER1_ID_STRING, (new Date(System.currentTimeMillis() + AUTHORIZATION_TOKEN_EXPIRATION_TIME)), UUID.randomUUID().toString()));
    }

    @Test
    void getPage() throws Exception {
        ResultActions actions = mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", DATE)
                .headers(jwtHeader))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        String bxCalories = actions.andReturn().getResponse().getContentAsString();
        assertEquals(USER1_BX_CALORIES, Integer.parseInt(bxCalories));
    }

    @Test
    void getPageTokenExpired() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", DATE)
                .headers(jwtExpiredHeader))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(BAD_TOKEN_ERROR))
                .andExpect(detailMessage(EXCEPTION_BAD_TOKEN));
    }

    @Test
    void getPageTokenInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(REST_URL)
                .param("date", DATE)
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