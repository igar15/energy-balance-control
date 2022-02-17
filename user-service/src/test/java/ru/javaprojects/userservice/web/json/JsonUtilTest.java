package ru.javaprojects.userservice.web.json;

import org.junit.jupiter.api.Test;
import ru.javaprojects.userservice.UserMatcher;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.to.NewUserTo;

import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.javaprojects.userservice.testdata.UserTestData.*;

class JsonUtilTest {

    @Test
    void readWriteValue() {
        String json = JsonUtil.writeValue(admin);
        System.out.println(json);
        User user = JsonUtil.readValue(json, User.class);
        UserMatcher.assertMatch(user, admin);
    }

    @Test
    void readWriteValues() {
        String json = JsonUtil.writeValue(List.of(admin, user));
        System.out.println(json);
        List<User> users = JsonUtil.readValues(json, User.class);
        UserMatcher.assertMatch(users, List.of(admin, user));
    }

    @Test
    void writeOnlyAccess() {
        String json = JsonUtil.writeValue(user);
        System.out.println(json);
        assertThat(json, not(containsString("password")));

        String jsonWithPassword = JsonUtil.writeAdditionProps(getNewUserTo(), "password", "newPass");
        System.out.println(jsonWithPassword);
        NewUserTo newUserTo = JsonUtil.readValue(jsonWithPassword, NewUserTo.class);
        assertEquals(newUserTo.getPassword(), "newPass");
    }

    @Test
    void readContentFromPage() {
        List<User> users = JsonUtil.readContentFromPage(JSON_USER_PAGE, User.class);
        UserMatcher.assertMatch(users, userDisabled, user);
    }
}