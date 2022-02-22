package ru.javaprojects.bxservice.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.javaprojects.bxservice.model.BasicExchange;
import ru.javaprojects.bxservice.repository.BasicExchangeRepository;
import ru.javaprojects.bxservice.service.client.UserServiceClient;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static ru.javaprojects.bxservice.testdata.BasicExchangeTestData.*;
import static ru.javaprojects.energybalancecontrolshared.test.TestData.ADMIN_ID;
import static ru.javaprojects.energybalancecontrolshared.test.TestData.USER_ID;

@SpringBootTest
@ActiveProfiles("dev")
@Sql(scripts = "classpath:data.sql", config = @SqlConfig(encoding = "UTF-8"))
@TestPropertySource(locations = "classpath:test.properties")
class BasicExchangeServiceTest {

    @Autowired
    private BasicExchangeService service;

    @Autowired
    private BasicExchangeRepository repository;

    @Mock
    private UserServiceClient userServiceClient;

    @PostConstruct
    private void setupUserServiceClient() {
        Mockito.when(userServiceClient.getUserBxDetails(USER_ID)).thenReturn(userBxDetails);
        Mockito.when(userServiceClient.getUserBxDetails(ADMIN_ID)).thenReturn(adminBxDetails);
        service.setUserServiceClient(userServiceClient);
    }

    @Test
    void getCalories() {
        Integer bxCalories = service.getBxCalories(FEBRUARY_6_2022, USER_ID);
        assertEquals(USER_BX_CALORIES, bxCalories);
    }

    @Test
    void getCaloriesWhenBasicExchangeDoesNotExist() {
        Integer bxCalories = service.getBxCalories(LocalDate.now(), USER_ID);
        assertEquals(USER_BX_CALORIES, bxCalories);
        assertTrue(repository.findByUserIdAndDate(USER_ID, LocalDate.now()).isPresent());
    }

    @Test
    void create() {
        service.create(LocalDate.now(), USER_ID);
        BasicExchange basicExchange = repository.findByUserIdAndDate(USER_ID, LocalDate.now()).get();
        assertEquals(LocalDate.now(), basicExchange.getDate());
        assertEquals(USER_BX_CALORIES, basicExchange.getCalories());
    }

    @Test
    void duplicateDateCreate() {
        assertThrows(DataAccessException.class, () -> service.create(FEBRUARY_6_2022, USER_ID));
    }

    @Test
    void duplicateDateCreateDifferentUser() {
        assertDoesNotThrow(() -> service.create(FEBRUARY_7_2022, ADMIN_ID));
    }

    @Test
    void updateBasicExchanges() {
        Mockito.when(userServiceClient.getUserBxDetails(USER_ID)).thenReturn(updatedUserBxDetails);
        service.updateBasicExchanges(FEBRUARY_6_2022, USER_ID);
        assertEquals(USER_BX_CALORIES, repository.findByUserIdAndDate(USER_ID, FEBRUARY_5_2022).get().getCalories());
        assertEquals(UPDATED_USER_BX_CALORIES, repository.findByUserIdAndDate(USER_ID, FEBRUARY_6_2022).get().getCalories());
        assertEquals(UPDATED_USER_BX_CALORIES, repository.findByUserIdAndDate(USER_ID, FEBRUARY_7_2022).get().getCalories());
        Mockito.when(userServiceClient.getUserBxDetails(USER_ID)).thenReturn(userBxDetails);
    }

    @Test
    void updateBasicExchangesWhenBasicExchangesNotFound() {
        Mockito.when(userServiceClient.getUserBxDetails(USER_ID)).thenReturn(updatedUserBxDetails);
        service.updateBasicExchanges(LocalDate.now(), USER_ID);
        assertTrue(repository.findAllByUserIdAndDateGreaterThanEqual(USER_ID, LocalDate.now()).isEmpty());
        assertEquals(USER_BX_CALORIES, repository.findByUserIdAndDate(USER_ID, FEBRUARY_5_2022).get().getCalories());
        assertEquals(USER_BX_CALORIES, repository.findByUserIdAndDate(USER_ID, FEBRUARY_6_2022).get().getCalories());
        assertEquals(USER_BX_CALORIES, repository.findByUserIdAndDate(USER_ID, FEBRUARY_7_2022).get().getCalories());
        Mockito.when(userServiceClient.getUserBxDetails(USER_ID)).thenReturn(userBxDetails);
    }

    @Test
    void deleteAll() {
        service.deleteAll(USER_ID);
        assertTrue(repository.findAllByUserId(USER_ID).isEmpty());
        assertFalse(repository.findAllByUserId(ADMIN_ID).isEmpty());
    }
}