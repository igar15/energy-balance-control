package ru.javaprojects.userservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.userservice.TestUtil;
import ru.javaprojects.userservice.UserMatcher;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.service.UserService;
import ru.javaprojects.userservice.to.NewUserTo;
import ru.javaprojects.userservice.to.UserTo;
import ru.javaprojects.userservice.util.exception.NotFoundException;
import ru.javaprojects.userservice.web.json.JsonUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javaprojects.userservice.testdata.UserTestData.*;
import static ru.javaprojects.userservice.util.exception.ErrorType.*;
import static ru.javaprojects.userservice.web.AppExceptionHandler.*;
import static ru.javaprojects.userservice.web.security.JwtProvider.AUTHORIZATION_TOKEN_HEADER;

class ProfileRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = ProfileRestController.REST_URL + '/';

    @Autowired
    private UserService service;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void login() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "login")
                .param("email", user.getEmail())
                .param("password", user.getPassword()))
                .andExpect(status().isOk())
                .andExpect(header().exists(AUTHORIZATION_TOKEN_HEADER))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(UserMatcher.contentJson(user));
    }

    @Test
    void loginFailed() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "login")
                .param("email", user.getEmail())
                .param("password", "wrongPassword"))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist(AUTHORIZATION_TOKEN_HEADER))
                .andExpect(errorType(BAD_CREDENTIALS_ERROR));
    }

    @Test
    void loginDisabled() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "login")
                .param("email", userDisabled.getEmail())
                .param("password", userDisabled.getPassword()))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(AUTHORIZATION_TOKEN_HEADER))
                .andExpect(errorType(DISABLED_ERROR))
                .andExpect(detailMessage(EXCEPTION_DISABLED));
    }

    @Test
    void register() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.post(REST_URL + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewUserTo())))
                .andExpect(status().isCreated());
        User created = TestUtil.readFromJson(actions, User.class);
        long newId = created.id();
        User newUser = getNewForRegister();
        newUser.setId(newId);
        UserMatcher.assertMatch(created, newUser);
        UserMatcher.assertMatch(service.get(newId), newUser);
    }

    @Test
    void registerInvalid() throws Exception {
        NewUserTo newUserTo = getNewUserTo();
        newUserTo.setName(" ");
        perform(MockMvcRequestBuilders.post(REST_URL + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newUserTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void registerDuplicateEmail() throws Exception {
        NewUserTo newUserTo = getNewUserTo();
        newUserTo.setEmail(user.getEmail());
        perform(MockMvcRequestBuilders.post(REST_URL + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newUserTo)))
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {"ROLE_USER"})
    void registerNotAnonymous() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + "register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewUserTo())))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(EXCEPTION_ACCESS_DENIED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {"ROLE_USER"})
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(UserMatcher.contentJson(user));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {"ROLE_USER"})
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedTo())))
                .andDo(print())
                .andExpect(status().isNoContent());
        UserMatcher.assertMatch(service.get(USER_ID), getUpdated());
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {"ROLE_USER"})
    void updateIdNotConsistent() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setId(USER_ID + 1);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    void updateUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedTo())))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {"ROLE_USER"})
    void updateInvalid() throws Exception {
        UserTo updatedTo = getUpdatedTo();
        updatedTo.setGrowth(10);
        mockMvc.perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {"ROLE_USER"})
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(USER_ID));
    }

    @Test
    void deleteUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {"ROLE_USER"})
    void changePassword() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + "password")
                .param("password", NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, service.get(USER_ID).getPassword()));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {"ROLE_USER"})
    void changePasswordInvalid() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + "password")
                .param("password", "pass"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_INVALID_PASSWORD));
    }

    @Test
    void changePasswordUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + "password")
                .param("password", NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    void resetPassword() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "password/reset")
                .param("email", user.getEmail()))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    void resetPasswordNotFound() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "password/reset")
                .param("email", NOT_FOUND_EMAIL))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {"ROLE_USER"})
    void resetPasswordNotAnonymous() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "password/reset")
                .param("email", user.getEmail()))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(EXCEPTION_ACCESS_DENIED));
    }

    @Test
    void sendEmailVerify() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/email/verify")
                .param("email", userDisabled.getEmail()))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    void sendEmailVerifyNotFound() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/email/verify")
                .param("email", NOT_FOUND_EMAIL))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void sendEmailVerifyAlreadyVerified() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/email/verify")
                .param("email", user.getEmail()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage("Email already verified:" + user.getEmail()));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {"ROLE_USER"})
    void sendEmailVerifyNotAnonymous() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + "/email/verify")
                .param("email", user.getEmail()))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(EXCEPTION_ACCESS_DENIED));
    }
}