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
import static ru.javaprojects.bxservice.testdata.UserTestData.USER1_ID;
import static ru.javaprojects.bxservice.testdata.UserTestData.USER2_ID;

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
        Mockito.when(userServiceClient.getUserBxDetails(USER1_ID)).thenReturn(user1BxDetails);
        Mockito.when(userServiceClient.getUserBxDetails(USER2_ID)).thenReturn(user2BxDetails);
        service.setUserServiceClient(userServiceClient);
    }

    @Test
    void getCalories() {
        Integer bxCalories = service.getBxCalories(FEBRUARY_6_2022, USER1_ID);
        assertEquals(USER1_BX_CALORIES, bxCalories);
    }

    @Test
    void getCaloriesWhenBasicExchangeDoesNotExist() {
        Integer bxCalories = service.getBxCalories(LocalDate.now(), USER1_ID);
        assertEquals(USER1_BX_CALORIES, bxCalories);
        assertTrue(repository.findByUserIdAndDate(USER1_ID, LocalDate.now()).isPresent());
    }

    @Test
    void create() {
        service.create(LocalDate.now(), USER1_ID);
        BasicExchange basicExchange = repository.findByUserIdAndDate(USER1_ID, LocalDate.now()).get();
        assertEquals(LocalDate.now(), basicExchange.getDate());
        assertEquals(USER1_BX_CALORIES, basicExchange.getCalories());
    }

    @Test
    void duplicateDateCreate() {
        assertThrows(DataAccessException.class, () -> service.create(FEBRUARY_6_2022, USER1_ID));
    }

    @Test
    void duplicateDateCreateDifferentUser() {
        assertDoesNotThrow(() -> service.create(FEBRUARY_7_2022, USER2_ID));
    }

    @Test
    void updateBasicExchanges() {
        Mockito.when(userServiceClient.getUserBxDetails(USER1_ID)).thenReturn(updatedUser1BxDetails);
        service.updateBasicExchanges(FEBRUARY_6_2022, USER1_ID);
        assertEquals(USER1_BX_CALORIES, repository.findByUserIdAndDate(USER1_ID, FEBRUARY_5_2022).get().getCalories());
        assertEquals(UPDATED_USER1_BX_CALORIES, repository.findByUserIdAndDate(USER1_ID, FEBRUARY_6_2022).get().getCalories());
        assertEquals(UPDATED_USER1_BX_CALORIES, repository.findByUserIdAndDate(USER1_ID, FEBRUARY_7_2022).get().getCalories());
        Mockito.when(userServiceClient.getUserBxDetails(USER1_ID)).thenReturn(user1BxDetails);
    }

    @Test
    void updateBasicExchangesWhenBasicExchangesNotFound() {
        Mockito.when(userServiceClient.getUserBxDetails(USER1_ID)).thenReturn(updatedUser1BxDetails);
        service.updateBasicExchanges(LocalDate.now(), USER1_ID);
        assertTrue(repository.findAllByUserIdAndDateGreaterThanEqual(USER1_ID, LocalDate.now()).isEmpty());
        assertEquals(USER1_BX_CALORIES, repository.findByUserIdAndDate(USER1_ID, FEBRUARY_5_2022).get().getCalories());
        assertEquals(USER1_BX_CALORIES, repository.findByUserIdAndDate(USER1_ID, FEBRUARY_6_2022).get().getCalories());
        assertEquals(USER1_BX_CALORIES, repository.findByUserIdAndDate(USER1_ID, FEBRUARY_7_2022).get().getCalories());
        Mockito.when(userServiceClient.getUserBxDetails(USER1_ID)).thenReturn(user1BxDetails);
    }

    @Test
    void deleteAll() {
        service.deleteAll(USER1_ID);
        assertTrue(repository.findAllByUserId(USER1_ID).isEmpty());
        assertFalse(repository.findAllByUserId(USER2_ID).isEmpty());
    }
}