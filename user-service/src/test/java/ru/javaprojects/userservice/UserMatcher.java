package ru.javaprojects.userservice;

import ru.javaprojects.userservice.model.User;

import java.util.Arrays;

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

//    public ResultMatcher contentJson(T expected) {
//        return result -> assertMatch(TestUtil.readFromJsonMvcResult(result, clazz), expected);
//    }
//
//    @SafeVarargs
//    public final ResultMatcher contentJson(T... expected) {
//        return contentJson(List.of(expected));
//    }
//
//    public ResultMatcher contentJson(Iterable<T> expected) {
//        return result -> assertMatch(TestUtil.readListFromJsonMvcResult(result, clazz), expected);
//    }
}