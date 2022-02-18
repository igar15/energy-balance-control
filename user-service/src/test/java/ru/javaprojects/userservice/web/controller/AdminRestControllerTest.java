package ru.javaprojects.userservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.userservice.TestUtil;
import ru.javaprojects.userservice.UserMatcher;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.to.AdminUserTo;
import ru.javaprojects.userservice.util.exception.NotFoundException;
import ru.javaprojects.userservice.web.json.JsonUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.userservice.testdata.UserTestData.*;
import static ru.javaprojects.userservice.util.exception.ErrorType.*;
import static ru.javaprojects.userservice.web.AppExceptionHandler.*;

class AdminRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = AdminRestController.REST_URL + '/';

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void getPage() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(REST_URL)
                .param(PAGE_NUMBER_PARAM, PAGE_NUMBER)
                .param(PAGE_SIZE_PARAM, PAGE_SIZE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        List<User> users = JsonUtil.readContentFromPage(actions.andReturn().getResponse().getContentAsString(), User.class);
        UserMatcher.assertMatch(users, userDisabled, user);
    }

    @Test
    void getPageUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param(PAGE_NUMBER_PARAM, PAGE_NUMBER)
                .param(PAGE_SIZE_PARAM, PAGE_SIZE))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void getPageForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL)
                .param(PAGE_NUMBER_PARAM, PAGE_NUMBER)
                .param(PAGE_SIZE_PARAM, PAGE_SIZE))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(EXCEPTION_ACCESS_DENIED));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void getPageByKeyword() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(REST_URL + SEARCH_BY_KEYWORD_ENDPOINT)
                .param(KEYWORD_PARAM, NAME_KEYWORD))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        List<User> users = JsonUtil.readContentFromPage(actions.andReturn().getResponse().getContentAsString(), User.class);
        UserMatcher.assertMatch(users, admin);
    }

    @Test
    void getPageByKeywordUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + SEARCH_BY_KEYWORD_ENDPOINT)
                .param(KEYWORD_PARAM, NAME_KEYWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void getPageByKeywordForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + SEARCH_BY_KEYWORD_ENDPOINT)
                .param(KEYWORD_PARAM, NAME_KEYWORD))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(EXCEPTION_ACCESS_DENIED));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void get() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + USER_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(UserMatcher.contentJson(user));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void getUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + USER_ID))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void getForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + ADMIN_ID))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(EXCEPTION_ACCESS_DENIED));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void createWithLocation() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeAdditionProps(getNew(), PASSWORD_PROPERTY_NAME, getNew().getPassword())))
                .andExpect(status().isCreated());
        User created = TestUtil.readFromJson(actions, User.class);
        long newId = created.id();
        User newUser = getNew();
        newUser.setId(newId);
        UserMatcher.assertMatch(created, newUser);
        UserMatcher.assertMatch(service.get(newId), newUser);
    }

    @Test
    void createUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeAdditionProps(getNew(), PASSWORD_PROPERTY_NAME, getNew().getPassword())))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void createForbidden() throws Exception {
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeAdditionProps(getNew(), PASSWORD_PROPERTY_NAME, getNew().getPassword())))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(EXCEPTION_ACCESS_DENIED));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void createInvalid() throws Exception {
        User newUser = getNew();
        newUser.setName(" ");
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeAdditionProps(newUser, PASSWORD_PROPERTY_NAME, newUser.getPassword())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicateEmail() throws Exception {
        User newUser = getNew();
        newUser.setEmail(user.getEmail());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeAdditionProps(newUser, PASSWORD_PROPERTY_NAME, newUser.getPassword())))
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getAdminUpdatedTo())))
                .andExpect(status().isNoContent());
        UserMatcher.assertMatch(service.get(USER_ID), getAdminUpdated());
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void updateIdNotConsistent() throws Exception {
        AdminUserTo adminUpdatedTo = getAdminUpdatedTo();
        adminUpdatedTo.setId(NOT_FOUND);
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(adminUpdatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void updateNotFound() throws Exception {
        AdminUserTo adminUpdatedTo = getAdminUpdatedTo();
        adminUpdatedTo.setId(NOT_FOUND);
        perform(MockMvcRequestBuilders.put(REST_URL + NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(adminUpdatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void updateUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getAdminUpdatedTo())))
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void updateForbidden() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(getAdminUpdatedTo())))
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(EXCEPTION_ACCESS_DENIED));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void updateInvalid() throws Exception {
        AdminUserTo adminUpdatedTo = getAdminUpdatedTo();
        adminUpdatedTo.setWeight(0);
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(adminUpdatedTo)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicateEmail() throws Exception {
        AdminUserTo adminUpdatedTo = getAdminUpdatedTo();
        adminUpdatedTo.setEmail(admin.getEmail());
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(adminUpdatedTo)))
                .andExpect(status().isConflict())
                .andExpect(errorType(DATA_ERROR))
                .andExpect(detailMessage(EXCEPTION_DUPLICATE_EMAIL));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(USER_ID));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void deleteUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void deleteForbidden() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + ADMIN_ID))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(EXCEPTION_ACCESS_DENIED));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void enable() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_DISABLED_ID))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(service.get(USER_DISABLED_ID).isEnabled());
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void enableNotFound() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    void enableUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_DISABLED_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void enableForbidden() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_DISABLED_ID))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(EXCEPTION_ACCESS_DENIED));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void changePassword() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID + "/" + CHANGE_PASSWORD_ENDPOINT)
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(passwordEncoder.matches(NEW_PASSWORD, service.get(USER_ID).getPassword()));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void changePasswordNotFound() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + NOT_FOUND + "/" + CHANGE_PASSWORD_ENDPOINT)
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(DATA_NOT_FOUND));
    }

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {ADMIN_ROLE})
    void changePasswordInvalid() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID + "/" + CHANGE_PASSWORD_ENDPOINT)
                .param(PASSWORD_PARAM, "pas"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(errorType(VALIDATION_ERROR))
                .andExpect(detailMessage(EXCEPTION_INVALID_PASSWORD));
    }

    @Test
    void changePasswordUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + USER_ID + "/" + CHANGE_PASSWORD_ENDPOINT)
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(errorType(UNAUTHORIZED_ERROR))
                .andExpect(detailMessage(EXCEPTION_NOT_AUTHORIZED));
    }

    @Test
    @WithMockCustomUser(userId = USER_ID_STRING, userRoles = {USER_ROLE})
    void changePasswordForbidden() throws Exception {
        perform(MockMvcRequestBuilders.patch(REST_URL + ADMIN_ID + "/" + CHANGE_PASSWORD_ENDPOINT)
                .param(PASSWORD_PARAM, NEW_PASSWORD))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(errorType(ACCESS_DENIED_ERROR))
                .andExpect(detailMessage(EXCEPTION_ACCESS_DENIED));
    }
}