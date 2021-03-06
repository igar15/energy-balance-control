package ru.javaprojects.userservice.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;
import static ru.javaprojects.energybalancecontrolshared.util.ValidationUtil.assureIdConsistent;
import static ru.javaprojects.energybalancecontrolshared.util.ValidationUtil.checkNew;
import static ru.javaprojects.userservice.web.swagger.OpenApiConfig.ALLOWED_ADMIN;


@RestController
@RequestMapping(value = AdminRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Secured("ROLE_ADMIN")
@Tag(name = "Admin Rest Controller" + ALLOWED_ADMIN)
public class AdminRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());
    static final String REST_URL = "/api/users";
    private final UserService service;

    public AdminRestController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(description = "Get user page" + ALLOWED_ADMIN)
    @Parameters({@Parameter(name = "page", in = QUERY, description = "Zero-based page index (0..N)", schema = @Schema(type = "integer", defaultValue = "0")),
                 @Parameter(name = "size", in = QUERY, description = "The size of the page to be returned", schema = @Schema(type = "integer", defaultValue = "20"))})
    public Page<User> getPage(@Parameter(hidden = true) Pageable pageable) {
        log.info("getPage(pageNumber={}, pageSize={})", pageable.getPageNumber(), pageable.getPageSize());
        return service.getPage(pageable);
    }

    @GetMapping("/by")
    @Operation(description = "Get user page by keyword" + ALLOWED_ADMIN)
    @Parameters({@Parameter(name = "page", in = QUERY, description = "Zero-based page index (0..N)", schema = @Schema(type = "integer", defaultValue = "0")),
                 @Parameter(name = "size", in = QUERY, description = "The size of the page to be returned", schema = @Schema(type = "integer", defaultValue = "20"))})
    public Page<User> getPageByKeyword(@Parameter(hidden = true) Pageable pageable, @RequestParam String keyword) {
        log.info("getPage(pageNumber={}, pageSize={}) by keyword {}", pageable.getPageNumber(), pageable.getPageSize(), keyword);
        return service.getPageByKeyword(keyword, pageable);
    }

    @GetMapping("/{id}")
    @Operation(description = "Get user" + ALLOWED_ADMIN)
    public User get(@PathVariable long id) {
        log.info("get {}", id);
        return service.get(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(description = "Create new user" + ALLOWED_ADMIN)
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
    @Operation(description = "Update user" + ALLOWED_ADMIN)
    public void update(@Valid @RequestBody AdminUserTo adminUserTo, @PathVariable long id) {
        log.info("update {} with id={}", adminUserTo, id);
        assureIdConsistent(adminUserTo, id);
        service.update(adminUserTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Delete user" + ALLOWED_ADMIN)
    public void delete(@PathVariable long id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Enable user" + ALLOWED_ADMIN)
    public void enable(@PathVariable long id) {
        log.info("enable {}", id);
        service.enable(id);
    }

    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "Change user's password" + ALLOWED_ADMIN)
    public void changePassword(@PathVariable long id, @RequestParam @Size(min = 5, max = 32) String password) {
        log.info("change password for user {}", id);
        service.changePassword(id, password);
    }
}