package ru.javaprojects.mealservice.web.security;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

public class AuthorizedUser extends org.springframework.security.core.userdetails.User {
    @Serial
    private static final long serialVersionUID = 1L;

    private final long id;

    public AuthorizedUser(String userId, Collection<? extends GrantedAuthority> authorities) {
        super(userId, null, authorities);
        id = Long.parseLong(userId);
    }

    public long getId() {
        return id;
    }
}