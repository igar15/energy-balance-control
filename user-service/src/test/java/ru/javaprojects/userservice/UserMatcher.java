package ru.javaprojects.userservice;

import org.springframework.test.web.servlet.ResultMatcher;
import ru.javaprojects.userservice.model.User;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMatcher {
    private UserMatcher() {
    }

    public static void assertMatch(User actual, User expected) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered", "password").isEqualTo(expected);
    }

    public static final void assertMatch(Iterable<User> actual, User... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<User> actual, Iterable<User> expected) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered", "password").isEqualTo(expected);
    }

    public static ResultMatcher contentJson(User expected) {
        return result -> assertMatch(TestUtil.readFromJsonMvcResult(result, User.class), expected);
    }

    @SafeVarargs
    public static final ResultMatcher contentJson(User... expected) {
        return contentJson(List.of(expected));
    }

    public static ResultMatcher contentJson(Iterable<User> expected) {
        return result -> assertMatch(TestUtil.readListFromJsonMvcResult(result, User.class), expected);
    }
}