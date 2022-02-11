package ru.javaprojects.mealservice.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.javaprojects.mealservice.web.security.JwtProvider;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtProvider jwtProvider;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void createAuthToken(HttpServletResponse response) {
        String token = jwtProvider.generateAuthorizationToken("200000","ROLE_USER");
        response.addHeader("token", token);
    }
}