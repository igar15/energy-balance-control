package ru.javaprojects.userservice.testdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.javaprojects.energybalancecontrolshared.test.TestMatcher;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.to.AdminUserTo;
import ru.javaprojects.userservice.to.NewUserTo;
import ru.javaprojects.userservice.to.UserBxDetails;
import ru.javaprojects.userservice.to.UserTo;

import java.util.List;
import java.util.Set;

import static ru.javaprojects.energybalancecontrolshared.test.TestData.ADMIN_ID;
import static ru.javaprojects.energybalancecontrolshared.test.TestData.USER_ID;
import static ru.javaprojects.userservice.model.Role.ADMIN;
import static ru.javaprojects.userservice.model.Role.USER;
import static ru.javaprojects.userservice.model.User.START_SEQ;
import static ru.javaprojects.userservice.model.User.Sex.MAN;
import static ru.javaprojects.userservice.model.User.Sex.WOMAN;

public class UserTestData {
    public static final String[] ignoringFields = {"registered", "password"};
    public static final TestMatcher<User> USER_MATCHER = TestMatcher.usingIgnoringFieldsComparator(User.class, ignoringFields);

    public static final long USER_DISABLED_ID = START_SEQ + 2;
    public static final String NOT_FOUND_EMAIL = "notfound@test.com";

    public static final User user = new User(USER_ID, "John Smith", "user@gmail.com", MAN, 90, 185, 34, "password", true, Set.of(USER));
    public static final User admin = new User(ADMIN_ID, "Viktor Wran", "admin@gmail.com", MAN, 85, 178, 41, "admin", true, Set.of(USER, ADMIN));
    public static final User userDisabled = new User(USER_DISABLED_ID, "Jack London", "jack@gmail.com", MAN, 83, 174, 38, "password", false, Set.of(USER));

    public static final UserBxDetails USER_BX_DETAILS = new UserBxDetails(user.getSex(), user.getWeight(), user.getGrowth(), user.getAge());

    public static final String PAGE_NUMBER = "0";
    public static final String PAGE_SIZE = "2";
    public static final Pageable PAGEABLE = PageRequest.of(Integer.parseInt(PAGE_NUMBER), Integer.parseInt(PAGE_SIZE));
    public static final Page<User> PAGE = new PageImpl<>(List.of(userDisabled, user), PAGEABLE, 3);
    public static final Page<User> PAGE_BY_NAME_KEYWORD = new PageImpl<>(List.of(admin), PAGEABLE, 1);
    public static final Page<User> PAGE_BY_EMAIL_KEYWORD = new PageImpl<>(List.of(userDisabled, user), PAGEABLE, 3);

    public static final String NAME_KEYWORD = "wran";
    public static final String EMAIL_KEYWORD = "gmail.com";

    public static final String NEW_PASSWORD = "newPassword";

    public static final String LOGIN_ENDPOINT = "login";
    public static final String REGISTER_ENDPOINT = "register";
    public static final String CHANGE_PASSWORD_ENDPOINT = "password";
    public static final String PASSWORD_RESET_ENDPOINT = "password/reset";
    public static final String EMAIL_VERIFY_ENDPOINT = "email/verify";
    public static final String SEARCH_BY_KEYWORD_ENDPOINT = "by";

    public static final String PASSWORD_PROPERTY_NAME = "password";

    public static final String EMAIL_PARAM = "email";
    public static final String PASSWORD_PARAM = "password";
    public static final String PAGE_NUMBER_PARAM = "page";
    public static final String PAGE_SIZE_PARAM = "size";
    public static final String KEYWORD_PARAM = "keyword";

    public static User getNew() {
        return new User(null, "new name", "new@test.com", WOMAN, 65, 170, 30, "newPassword", false, Set.of(USER));
    }

    public static User getUpdated() {
        return new User(USER_ID, "Updated name", "user@gmail.com", MAN, 95, 186, 35, "password", true, Set.of(USER));
    }

    public static UserTo getUpdatedTo() {
        return new UserTo(USER_ID, "Updated name", MAN, 95, 186, 35);
    }

    public static User getAdminUpdated() {
        return new User(USER_ID, "Updated name", "updated@test.com", MAN, 95, 186, 35, "password", true, Set.of(USER, ADMIN));
    }

    public static AdminUserTo getAdminUpdatedTo() {
        return new AdminUserTo(USER_ID, "Updated name", "updated@test.com", MAN, 95, 186, 35, Set.of(USER, ADMIN));
    }

    public static NewUserTo getNewUserTo() {
        return new NewUserTo("new username", "newuser@test.com", WOMAN, 60, 175, 30, "newPass");
    }

    public static User getNewForRegister() {
        return new User(null, "new username", "newuser@test.com", WOMAN, 60, 175, 30, "newPass", false, Set.of(USER));
    }
}