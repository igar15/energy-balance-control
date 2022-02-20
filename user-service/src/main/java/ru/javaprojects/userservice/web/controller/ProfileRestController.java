package ru.javaprojects.userservice.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtProvider;
import ru.javaprojects.energybalancecontrolshared.web.security.SecurityUtil;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.service.UserService;
import ru.javaprojects.userservice.to.NewUserTo;
import ru.javaprojects.userservice.to.UserTo;
import ru.javaprojects.userservice.web.security.AuthorizedUser;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.net.URI;

import static ru.javaprojects.energybalancecontrolshared.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.energybalancecontrolshared.web.security.JwtProvider.AUTHORIZATION_TOKEN_HEADER;
import static ru.javaprojects.userservice.util.UserUtil.createNewFromTo;

@RestController
@RequestMapping(value = ProfileRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class ProfileRestController {
    static final String REST_URL = "/api/profile";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserService service;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public ProfileRestController(UserService service, AuthenticationManager authenticationManager,
                                 JwtProvider jwtProvider) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestParam String email, @RequestParam String password) {
        log.info("login user {}", email);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email.toLowerCase(), password));
        User loggedUser = ((AuthorizedUser) authentication.getPrincipal()).getUser();
        HttpHeaders jwtHeader = new HttpHeaders();
        jwtHeader.add(AUTHORIZATION_TOKEN_HEADER, jwtProvider.generateAuthorizationToken(String.valueOf(loggedUser.getId())));
        return new ResponseEntity<>(loggedUser, jwtHeader, HttpStatus.OK);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> register(@Valid @RequestBody NewUserTo newUserTo) {
        log.info("register user {}", newUserTo);
        User created = service.create(createNewFromTo(newUserTo));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @GetMapping
    public User get() {
        long userId = SecurityUtil.authUserId();
        log.info("get {}", userId);
        return service.get(userId);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody UserTo userTo) {
        long userId = SecurityUtil.authUserId();
        log.info("update {} with id={}", userTo, userId);
        assureIdConsistent(userTo, userId);
        service.update(userTo);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() {
        long userId = SecurityUtil.authUserId();
        log.info("delete {}", userId);
        service.delete(userId);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestParam @Size(min = 5, max = 32) String password) {
        long userId = SecurityUtil.authUserId();
        log.info("change password for user {}", userId);
        service.changePassword(userId, password);
    }

    @PutMapping("/password/reset")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void resetPassword(@RequestParam String email) {
        log.info("reset password for user {}", email);
        service.resetPassword(email);
    }

    @PutMapping("/email/verify")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendEmailVerify(@RequestParam String email) {
        log.info("send email verify for user {}", email);
        service.sendEmailVerify(email);
    }
}