package ru.javaprojects.mealservice.web.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static long authUserId() {
        Authentication authentication = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication(), "No authorized user found");
        return Long.parseLong((String) authentication.getPrincipal());
    }
}