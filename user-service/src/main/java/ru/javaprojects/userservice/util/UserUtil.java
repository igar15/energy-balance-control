package ru.javaprojects.userservice.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.to.AdminUserTo;
import ru.javaprojects.userservice.to.BaseUserTo;
import ru.javaprojects.userservice.to.NewUserTo;
import ru.javaprojects.userservice.to.UserBxDetails;

import java.util.Set;

import static ru.javaprojects.userservice.model.Role.USER;

public class UserUtil {
    private UserUtil() {
    }

    public static User prepareToSave(User user, PasswordEncoder passwordEncoder) {
        String password = user.getPassword();
        user.setPassword(StringUtils.hasText(password) ? passwordEncoder.encode(password) : password);
        user.setEmail(user.getEmail().toLowerCase());
        return user;
    }

    public static User createNewFromTo(NewUserTo newUserTo) {
        return new User(null, newUserTo.getName(), newUserTo.getEmail(), newUserTo.getSex(), newUserTo.getWeight(),
                 newUserTo.getGrowth(), newUserTo.getAge(), newUserTo.getPassword(), false, Set.of(USER));
    }

    public static boolean updateFromTo(User user, BaseUserTo userTo) {
        boolean userDetailsChanged = isUserDetailsChanged(user, userTo);
        user.setName(userTo.getName());
        user.setSex(userTo.getSex());
        user.setWeight(userTo.getWeight());
        user.setGrowth(userTo.getGrowth());
        user.setAge(userTo.getAge());
        return userDetailsChanged;
    }

    public static boolean updateFromTo(User user, AdminUserTo adminUserTo) {
        user.setEmail(adminUserTo.getEmail());
        user.setRoles(adminUserTo.getRoles());
        return updateFromTo(user, (BaseUserTo) adminUserTo);
    }

    public static UserBxDetails getBxDetails(User user) {
        return new UserBxDetails(user.getSex(), user.getWeight(), user.getGrowth(), user.getAge());
    }

    static boolean isUserDetailsChanged(User user, BaseUserTo userTo) {
        return !user.getSex().equals(userTo.getSex()) ||
               !user.getWeight().equals(userTo.getWeight()) ||
               !user.getGrowth().equals(userTo.getGrowth()) ||
               !user.getAge().equals(userTo.getAge());
    }
}