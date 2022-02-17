package ru.javaprojects.userservice.web.security;

import ru.javaprojects.userservice.model.User;

import java.io.Serializable;

public class AuthorizedUser extends org.springframework.security.core.userdetails.User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long id;
    private final User user;

    public AuthorizedUser(User user) {
        super(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, user.getRoles());
        id = user.getId();
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }
}