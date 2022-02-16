package ru.javaprojects.userservice.testdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.to.UserTo;

import java.util.List;
import java.util.Set;

import static ru.javaprojects.userservice.model.Role.ADMIN;
import static ru.javaprojects.userservice.model.Role.USER;
import static ru.javaprojects.userservice.model.User.START_SEQ;
import static ru.javaprojects.userservice.model.User.Sex.MAN;
import static ru.javaprojects.userservice.model.User.Sex.WOMAN;

public class UserTestData {
    public static final long USER_ID = START_SEQ;
    public static final long ADMIN_ID = START_SEQ + 1;
    public static final long USER_DISABLED_ID = START_SEQ + 2;
    public static final long NOT_FOUND = 10;
    public static final String NOT_FOUND_EMAIL = "notfound@test.com";

    public static final User user = new User(USER_ID, "John Smith", "user@gmail.com", MAN, 90, 185, 34, "password", true, Set.of(USER));
    public static final User admin = new User(ADMIN_ID, "Viktor Wran", "admin@gmail.com", MAN, 85, 178, 41, "admin", true, Set.of(USER, ADMIN));
    public static final User userDisabled = new User(USER_DISABLED_ID, "Jack London", "jack@gmail.com", MAN, 83, 174, 38, "password", false, Set.of(USER));

    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "2";
    public static final Pageable PAGEABLE = PageRequest.of(Integer.parseInt(PAGE_NUMBER), Integer.parseInt(PAGE_SIZE));
    public static final Page<User> PAGE = new PageImpl<>(List.of(userDisabled, user), PAGEABLE, 3);
    public static final Page<User> PAGE_BY_NAME_KEYWORD = new PageImpl<>(List.of(admin), PAGEABLE, 1);
    public static final Page<User> PAGE_BY_EMAIL_KEYWORD = new PageImpl<>(List.of(userDisabled, user), PAGEABLE, 3);

    public static final String NAME_KEYWORD = "wran";
    public static final String EMAIL_KEYWORD = "gmail.com";

    public static final String NEW_PASSWORD = "newPassword";


    public static User getNew() {
        return new User(null, "new name", "new@test.com", WOMAN, 65, 170, 30, "newPassword", false, Set.of(USER));
    }

    public static User getUpdated() {
        return new User(USER_ID, "Updated name", "user@gmail.com", MAN, 95, 186, 35, "password", true, Set.of(USER));
    }

    public static UserTo getUpdatedTo() {
        return new UserTo(USER_ID, "Updated name", MAN, 95, 186, 35, Set.of(USER));
    }
}
