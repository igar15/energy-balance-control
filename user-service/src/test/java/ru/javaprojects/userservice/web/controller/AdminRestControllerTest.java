package ru.javaprojects.userservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.userservice.UserMatcher;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.service.UserService;
import ru.javaprojects.userservice.web.json.JsonUtil;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javaprojects.userservice.testdata.UserTestData.*;

class AdminRestControllerTest extends AbstractControllerTest {
    private static final String REST_URL = AdminRestController.REST_URL + '/';

    @Autowired
    private UserService service;

    @Test
    @WithMockCustomUser(userId = ADMIN_ID_STRING, userRoles = {"ROLE_ADMIN"})
    void getPage() throws Exception {
        ResultActions actions = perform(MockMvcRequestBuilders.get(REST_URL)
                .param("page", PAGE_NUMBER)
                .param("size", PAGE_SIZE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        List<User> users = JsonUtil.readContentFromPage(actions.andReturn().getResponse().getContentAsString(), User.class);
        UserMatcher.assertMatch(users, userDisabled, user);
    }

    @Test
    void getPageByKeyword() {
    }

    @Test
    void tesst() {
        JsonUtil.readValue("", User.class);
    }

    @Test
    void get() {
    }

    @Test
    void createWithLocation() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void enable() {
    }

    @Test
    void changePassword() {
    }
}