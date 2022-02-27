package ru.javaprojects.gatewayserver.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.javaprojects.energybalancecontrolshared.test.WithMockCustomUser;

import static ru.javaprojects.energybalancecontrolshared.test.TestData.*;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("dev")
@TestPropertySource(locations = "classpath:test.properties")
public class GatewayActuatorTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void actuator() {
        webTestClient.get().uri(ACTUATOR_PATH)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void actuatorUnAuth() {
        webTestClient.get().uri(ACTUATOR_PATH)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void actuatorNotAdmin() {
        webTestClient.get().uri(ACTUATOR_PATH)
                .exchange()
                .expectStatus().isForbidden();
    }
}