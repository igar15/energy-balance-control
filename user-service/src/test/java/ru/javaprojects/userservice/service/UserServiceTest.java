package ru.javaprojects.userservice.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.javaprojects.energybalancecontrolshared.util.ValidationUtil;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.userservice.messaging.MessageSender;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.to.UserTo;
import ru.javaprojects.userservice.util.exception.EmailVerificationException;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.javaprojects.energybalancecontrolshared.test.TestData.*;
import static ru.javaprojects.userservice.model.Role.USER;
import static ru.javaprojects.userservice.model.User.Sex.MAN;
import static ru.javaprojects.userservice.testdata.UserTestData.*;

@SpringBootTest
@ActiveProfiles("dev")
@Sql(scripts = "classpath:data.sql", config = @SqlConfig(encoding = "UTF-8"))
@TestPropertySource(locations = "classpath:test.properties")
class UserServiceTest {

    @Autowired
    private UserService service;

    @Mock
    private MessageSender messageSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    void setupUserService() {
        service.setMessageSender(messageSender);
    }

    @Test
    void createWithSendingEmailVerifyMessage() {
        User created = service.create(getNew());
        long newId = created.id();
        User newUser = getNew();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
        Mockito.verify(messageSender, Mockito.times(1)).sendEmailVerifyMessage(created.getEmail());
    }

    @Test
    void createWithoutSendingEmailVerifyMessage() {
        User newUser = getNew();
        newUser.setEnabled(true);
        service.create(newUser);
        Mockito.verify(messageSender, Mockito.times(0)).sendEmailVerifyMessage(Mockito.anyString());
    }

    @Test
    void duplicateMailCreate() {
        User newUser = getNew();
        newUser.setEmail(user.getEmail());
        assertThrows(DataAccessException.class, () -> service.create(newUser));
    }

    @Test
    void createInvalid() {
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, " ", "email@test.com", MAN, 90, 180, 40, "password", false, Set.of(USER))));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "new name", " ", MAN, 90, 180, 40, "password", false, Set.of(USER))));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "new name", "email@test.com", null, 90, 180, 40, "password", false, Set.of(USER))));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "new name", "email@test.com", MAN, 5, 180, 40, "password", false, Set.of(USER))));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "new name", "email@test.com", MAN, 90, 20, 40, "password", false, Set.of(USER))));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "new name", "email@test.com", MAN, 90, 180, 200, "password", false, Set.of(USER))));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "new name", "email@test.com", MAN, 90, 180, 40, "", false, Set.of(USER))));
        validateRootCause(ConstraintViolationException.class, () -> service.create(new User(null, "new name", "email@test.com", MAN, 90, 180, 40, "password", false, Set.of())));
    }

    @Test
    void get() {
        User user = service.get(ADMIN_ID);
        USER_MATCHER.assertMatch(user, admin);
    }

    @Test
    void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND));
    }

    @Test
    void getByEmail() {
        User user = service.getByEmail(admin.getEmail());
        USER_MATCHER.assertMatch(user, admin);
    }

    @Test
    void getByEmailNotFound() {
        assertThrows(NotFoundException.class, () -> service.getByEmail(NOT_FOUND_EMAIL));
    }

    @Test
    void getPage() {
        Page<User> userPage = service.getPage(PAGEABLE);
        assertThat(userPage).usingRecursiveComparison().ignoringFields(ignoringFields).isEqualTo(PAGE);
        USER_MATCHER.assertMatch(userPage.getContent(), userDisabled, user);
    }

    @Test
    void getPageByNameKeyword() {
        Page<User> userPage = service.getPageByKeyword(NAME_KEYWORD, PAGEABLE);
        assertThat(userPage).usingRecursiveComparison().ignoringFields(ignoringFields).isEqualTo(PAGE_BY_NAME_KEYWORD);
        USER_MATCHER.assertMatch(userPage.getContent(), admin);
    }

    @Test
    void getPageByEmailKeyword() {
        Page<User> userPage = service.getPageByKeyword(EMAIL_KEYWORD, PAGEABLE);
        assertThat(userPage).usingRecursiveComparison().ignoringFields(ignoringFields).isEqualTo(PAGE_BY_EMAIL_KEYWORD);
        USER_MATCHER.assertMatch(userPage.getContent(), userDisabled, user);
    }

    @Test
    void delete() {
        service.delete(USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USER_ID));
        Mockito.verify(messageSender, Mockito.times(1)).sendUserDeletedMessage(user.getEmail(), USER_ID);
    }

    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND));
        Mockito.verify(messageSender, Mockito.times(0)).sendUserDeletedMessage(Mockito.anyString(), Mockito.anyLong());
    }

    @Test
    void updateWithSendingDateMessage() {
        service.update(getUpdatedTo());
        USER_MATCHER.assertMatch(service.get(USER_ID), getUpdated());
        Mockito.verify(messageSender, Mockito.times(1)).sendDateMessage(LocalDate.now(), USER_ID);
    }

    @Test
    void updateWithoutSendingDateMessage() {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setWeight(user.getWeight());
        updatedTo.setGrowth(user.getGrowth());
        updatedTo.setAge(user.getAge());
        service.update(updatedTo);
        Mockito.verify(messageSender, Mockito.times(0)).sendDateMessage(Mockito.any(LocalDate.class), Mockito.anyLong());
    }

    @Test
    void updateWithAdminUserTo() {
        service.update(getAdminUpdatedTo());
        USER_MATCHER.assertMatch(service.get(USER_ID), getAdminUpdated());
        Mockito.verify(messageSender, Mockito.times(1)).sendDateMessage(LocalDate.now(), USER_ID);
    }

    @Test
    void updateNotFound() {
        UserTo updatedTo  = getUpdatedTo();
        updatedTo.setId(NOT_FOUND);
        assertThrows(NotFoundException.class, () -> service.update(updatedTo));
    }

    @Test
    void changePasswordById() {
        service.changePassword(USER_ID, NEW_PASSWORD);
        User user = service.get(USER_ID);
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, user.getPassword()));
    }

    @Test
    void changePasswordByIdNotFound() {
        assertThrows(NotFoundException.class, () -> service.changePassword(NOT_FOUND, NEW_PASSWORD));
    }

    @Test
    void changePasswordByEmail() {
        service.changePassword(user.getEmail(), NEW_PASSWORD);
        User user = service.get(USER_ID);
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, user.getPassword()));
    }

    @Test
    void changePasswordByEmailNotFound() {
        assertThrows(NotFoundException.class, () -> service.changePassword(NOT_FOUND_EMAIL, NEW_PASSWORD));
    }

    @Test
    void enableByEmail() {
        service.enable(userDisabled.getEmail());
        assertTrue(service.get(USER_DISABLED_ID).isEnabled());
    }

    @Test
    void enableByEmailNotFound() {
        assertThrows(NotFoundException.class, () -> service.enable(NOT_FOUND_EMAIL));
    }

    @Test
    void enableById() {
        service.enable(userDisabled.getId());
        assertTrue(service.get(USER_DISABLED_ID).isEnabled());
    }

    @Test
    void enableByIdNotFound() {
        assertThrows(NotFoundException.class, () -> service.enable(NOT_FOUND));
    }

    @Test
    void sendEmailVerify() {
        service.sendEmailVerify(userDisabled.getEmail());
        Mockito.verify(messageSender, Mockito.times(1)).sendEmailVerifyMessage(userDisabled.getEmail());
    }

    @Test
    void sendEmailVerifyAlreadyVerified() {
        assertThrows(EmailVerificationException.class, () -> service.sendEmailVerify(user.getEmail()));
        Mockito.verify(messageSender, Mockito.times(0)).sendEmailVerifyMessage(Mockito.anyString());
    }

    @Test
    void sendEmailVerifyNotFound() {
        assertThrows(NotFoundException.class, () -> service.sendEmailVerify(NOT_FOUND_EMAIL));
        Mockito.verify(messageSender, Mockito.times(0)).sendEmailVerifyMessage(Mockito.anyString());
    }

    @Test
    void resetPassword() {
        service.resetPassword(user.getEmail());
        Mockito.verify(messageSender, Mockito.times(1)).sendPasswordResetMessage(user.getEmail());
    }

    @Test
    void resetPasswordNotFound() {
        assertThrows(NotFoundException.class, () -> service.resetPassword(NOT_FOUND_EMAIL));
        Mockito.verify(messageSender, Mockito.times(0)).sendPasswordResetMessage(Mockito.anyString());
    }

    private <T extends Throwable> void validateRootCause(Class<T> rootExceptionClass, Runnable runnable) {
        assertThrows(rootExceptionClass, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw ValidationUtil.getRootCause(e);
            }
        });
    }
}