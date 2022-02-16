package ru.javaprojects.userservice.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.to.UserTo;

public class UserUtil {
    private UserUtil() {
    }

    public static User prepareToSave(User user, PasswordEncoder passwordEncoder) {
        String password = user.getPassword();
        user.setPassword(StringUtils.hasText(password) ? passwordEncoder.encode(password) : password);
        user.setEmail(user.getEmail().toLowerCase());
        return user;
    }
    public static boolean updateFromTo(User user, UserTo userTo) {
        boolean userDetailsChanged = isUserDetailsChanged(user, userTo);
        user.setName(userTo.getName());
        user.setSex(userTo.getSex());
        user.setWeight(userTo.getWeight());
        user.setGrowth(userTo.getGrowth());
        user.setAge(userTo.getAge());
        user.setRoles(userTo.getRoles());
        return userDetailsChanged;
    }

    static boolean isUserDetailsChanged(User user, UserTo userTo) {
        return !user.getSex().equals(userTo.getSex()) ||
               !user.getWeight().equals(userTo.getWeight()) ||
               !user.getGrowth().equals(userTo.getGrowth()) ||
               !user.getAge().equals(userTo.getAge());
    }
}