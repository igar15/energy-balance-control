package ru.javaprojects.userservice.util;

import org.junit.jupiter.api.Test;
import ru.javaprojects.userservice.to.UserTo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.javaprojects.userservice.model.User.Sex.WOMAN;
import static ru.javaprojects.userservice.testdata.UserTestData.user;

class UserUtilTest {

    @Test
    void isUserDetailsChanged() {
        assertFalse(UserUtil.isUserDetailsChanged(user, getUserTo()));
        UserTo userTo = getUserTo();
        userTo.setName("new name");
        assertFalse(UserUtil.isUserDetailsChanged(user, userTo));
        userTo = getUserTo();
        userTo.setSex(WOMAN);
        assertTrue(UserUtil.isUserDetailsChanged(user, userTo));
        userTo = getUserTo();
        userTo.setWeight(100);
        assertTrue(UserUtil.isUserDetailsChanged(user, userTo));
        userTo = getUserTo();
        userTo.setGrowth(200);
        assertTrue(UserUtil.isUserDetailsChanged(user, userTo));
        userTo = getUserTo();
        userTo.setAge(100);
        assertTrue(UserUtil.isUserDetailsChanged(user, userTo));
        userTo = getUserTo();
        userTo.setSex(WOMAN);
        userTo.setWeight(100);
        userTo.setGrowth(200);
        userTo.setAge(100);
        assertTrue(UserUtil.isUserDetailsChanged(user, userTo));
    }

    private UserTo getUserTo() {
        return new UserTo(user.getId(), user.getName(), user.getSex(), user.getWeight(), user.getGrowth(), user.getAge());
    }
}