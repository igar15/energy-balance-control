package ru.javaprojects.userservice.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.service.UserService;
import ru.javaprojects.userservice.to.AdminUserTo;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.net.URI;

import static ru.javaprojects.userservice.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.userservice.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = AdminRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Secured("ROLE_ADMIN")
public class AdminRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/api/users";
    private final UserService service;

    public AdminRestController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public Page<User> getPage(Pageable pageable) {
        log.info("getPage(pageNumber={}, pageSize={})", pageable.getPageNumber(), pageable.getPageSize());
        return service.getPage(pageable);
    }

    @GetMapping("/by")
    public Page<User> getPageByKeyword(Pageable pageable, @RequestParam String keyword) {
        log.info("getPage(pageNumber={}, pageSize={}) by keyword {}", pageable.getPageNumber(), pageable.getPageSize(), keyword);
        return service.getPageByKeyword(keyword, pageable);
    }

    @GetMapping("/{id}")
    public User get(@PathVariable long id) {
        log.info("get {}", id);
        return service.get(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createWithLocation(@Valid @RequestBody User user) {
        log.info("create {}", user);
        checkNew(user);
        User created = service.create(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Valid @RequestBody AdminUserTo adminUserTo, @PathVariable long id) {
        log.info("update {} with id={}", adminUserTo, id);
        assureIdConsistent(adminUserTo, id);
        service.update(adminUserTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable long id) {
        log.info("enable {}", id);
        service.enable(id);
    }

    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable long id, @RequestParam @Size(min = 5, max = 32) String password) {
        log.info("change password for user {}", id);
        service.changePassword(id, password);
    }
}