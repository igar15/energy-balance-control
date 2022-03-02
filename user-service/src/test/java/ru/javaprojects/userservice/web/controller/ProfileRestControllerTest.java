package ru.javaprojects.userservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.energybalancecontrolshared.test.TestUtil;
import ru.javaprojects.energybalancecontrolshared.test.WithMockCustomUser;
import ru.javaprojects.energybalancecontrolshared.util.exception.NotFoundException;
import ru.javaprojects.energybalancecontrolshared.web.json.JsonUtil;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.to.NewUserTo;
import ru.javaprojects.userservice.to.UserBxDetails;
import ru.javaprojects.userservice.to.UserTo;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javaprojects.energybalancecontrolshared.test.TestData.*;
import static ru.javaprojects.energybalancecontrolshared.util.exception.ErrorType.*;
import static ru.javaprojects.energybalancecontrolshared.web.security.JwtProvider.AUTHORIZATION_TOKEN_HEADER;
import static ru.javaprojects.energybalancecontrolshared.web.security.SecurityConstants.*;
import static ru.javaprojects.userservice.model.Role.USER;
import static ru.javaprojects.userservice.testdata.UserTestData.*;
import static ru.javaprojects.userservice.web.AppExceptionHandler.EXCEPTION_DUPLICATE_EMAIL;
import static ru.javaprojects.userservice.web.AppExceptionHandler.EXCEPTION_INVALID_PASSWORD;

class ProfileRestControllerTest extends AbstractUserRestControllerTest {
    private static final String REST_URL = ProfileRestController.REST_URL + '/';

    @Test
    void login() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.post(REST_URL + LOGIN_ENDPOINT)
                .param(EMAIL_PARAM, user.getEmail())
                .param(PASSWORD_PARAM, user.getPassword()))
                .andExpect(status().isOk())
                .andExpect(header().exists(AUTHORIZATION_TOKEN_HEADER))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(user));
        String token = actions.andReturn().getResponse().getHeader(AUTHORIZATION_TOKEN_HEADER);
        String userId = jwtProvider.getSubject(token);
        List<GrantedAuthority> userAuthorities = jwtProvider.getAuthorities(token);
        assertEquals(USER_ID_STRING, userId);
        assertEquals(List.of(new SimpleGrantedAuthority(USER.getAuthority())), userAuthorities);
    }

    @Test
    void loginFailed() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + LOGIN_ENDPOINT)
                .param(EMAIL_PARAM, user.getEmail())
                .param(PASSWORD_PARAM, "wrongPassword"))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist(AUTHORIZATION_TOKEN_HEADER))
                .andExpect(errorType(BAD_CREDENTIALS_ERROR));
    }

    @Test
    void loginDisabled() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + LOGIN_ENDPOINT)
                .param(EMAIL_PARAM, userDisabled.getEmail())
                .param(PASSWORD_PARAM, userDisabled.getPassword()))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(AUTHORIZATION_TOKEN_HEADER))
                .andExpect(errorType(DISABLED_ERROR))
                .andExpect(detailMessage(DISABLED));
    }

    @Test
    void register() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.post(REST_URL + REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewUserTo())))
                .andExpect(status().isCreated());
        User created = TestUtil.readFromJson(actions, User.class);
        long newId = created.id();
        User newUser = getNewForRegister();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(service.get(newId), newUser);
    }

    @Test
    void registerInvalid() throws Exception {
        NewUserTo newUserTo = getNewUserTo();
        newUserTo.setName(" ");
        perform(MockMvcRequestBuilders.post(REST_URL + REGISTER_ENDPOINT)
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
        perform(MockMvcRequestBuilders.post(REST_URL + REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newUserTo)))
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void registerNotAnonymous() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL + REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getNewUserTo())))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(ACCESS_DENIED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(user));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void getBxDetails() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(REST_URL + "bx-details"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        UserBxDetails userBxDetails = TestUtil.readFromJson(actions, UserBxDetails.class);
        assertThat(userBxDetails).usingRecursiveComparison().isEqualTo(USER_BX_DETAILS);
    }

    @Test
    void getBxDetailsUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "bx-details"))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getUpdatedTo())))
                .andDo(print())
                .andExpect(status().isNoContent());
        USER_MATCHER.assertMatch(service.get(USER_ID), getUpdated());
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
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
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
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
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
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
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void changePassword() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + CHANGE_PASSWORD_ENDPOINT)
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, service.get(USER_ID).getPassword()));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void changePasswordInvalid() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + CHANGE_PASSWORD_ENDPOINT)
                .param(PASSWORD_PARAM, "pass"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_INVALID_PASSWORD));
    }

    @Test
    void changePasswordUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + CHANGE_PASSWORD_ENDPOINT)
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(NOT_AUTHORIZED));
    }

    @Test
    void resetPassword() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + PASSWORD_RESET_ENDPOINT)
                .param(EMAIL_PARAM, user.getEmail()))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    void resetPasswordNotFound() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + PASSWORD_RESET_ENDPOINT)
                .param(EMAIL_PARAM, NOT_FOUND_EMAIL))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void resetPasswordNotAnonymous() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + PASSWORD_RESET_ENDPOINT)
                .param(EMAIL_PARAM, user.getEmail()))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(ACCESS_DENIED));
    }

    @Test
    void sendEmailVerify() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + EMAIL_VERIFY_ENDPOINT)
                .param(EMAIL_PARAM, userDisabled.getEmail()))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    void sendEmailVerifyNotFound() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + EMAIL_VERIFY_ENDPOINT)
                .param(EMAIL_PARAM, NOT_FOUND_EMAIL))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void sendEmailVerifyAlreadyVerified() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + EMAIL_VERIFY_ENDPOINT)
                .param(EMAIL_PARAM, user.getEmail()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage("Email already verified:" + user.getEmail()));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void sendEmailVerifyNotAnonymous() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + EMAIL_VERIFY_ENDPOINT)
                .param(EMAIL_PARAM, user.getEmail()))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(ACCESS_DENIED));
    }
}