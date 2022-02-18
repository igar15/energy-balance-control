package ru.javaprojects.userservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.userservice.messaging.MessageSender;
import ru.javaprojects.userservice.model.User;
import ru.javaprojects.userservice.repository.UserRepository;
import ru.javaprojects.userservice.to.AdminUserTo;
import ru.javaprojects.userservice.to.UserTo;
import ru.javaprojects.userservice.util.exception.EmailVerificationException;
import ru.javaprojects.userservice.util.exception.NotFoundException;

import java.time.LocalDate;

import static ru.javaprojects.userservice.util.UserUtil.prepareToSave;
import static ru.javaprojects.userservice.util.UserUtil.updateFromTo;

@Service
public class UserService {
    private static final String MUST_NOT_BE_NULL = " must not be null";
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private MessageSender messageSender;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, MessageSender messageSender) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.messageSender = messageSender;
    }

    public User create(User user) {
        Assert.notNull(user, "user" + MUST_NOT_BE_NULL);
        prepareToSave(user, passwordEncoder);
        repository.save(user);
        if (!user.isEnabled()) {
            messageSender.sendEmailVerifyMessage(user.getEmail());
        }
        return user;
    }

    public User get(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Not found user with id=" + id));
    }

    public User getByEmail(String email) {
        Assert.notNull(email, "email" + MUST_NOT_BE_NULL);
        return repository.findByEmail(email).orElseThrow(() -> new NotFoundException("Not found user with email=" + email));
    }

    public Page<User> getPage(Pageable pageable) {
        Assert.notNull(pageable, "pageable" + MUST_NOT_BE_NULL);
        return repository.findAllByOrderByNameAscEmail(pageable);
    }

    public Page<User> getPageByKeyword(String keyword, Pageable pageable) {
        Assert.notNull(keyword, "keyword" + MUST_NOT_BE_NULL);
        Assert.notNull(pageable, "pageable" + MUST_NOT_BE_NULL);
        return repository.findAllByNameContainsIgnoreCaseOrEmailContainsIgnoreCaseOrderByNameAscEmail(keyword, keyword, pageable);
    }

    public void delete(long id) {
        User user = get(id);
        repository.delete(user);
        messageSender.sendUserDeletedMessage(user.getEmail(), id);
    }

    @Transactional
    public void update(AdminUserTo adminUserTo) {
        Assert.notNull(adminUserTo, "adminUserTo" + MUST_NOT_BE_NULL);
        User user = get(adminUserTo.id());
        boolean userBxDetailsChanged = updateFromTo(user, adminUserTo);
        if (userBxDetailsChanged) {
            messageSender.sendDateMessage(LocalDate.now(), adminUserTo.id());
        }
    }

    @Transactional
    public void update(UserTo userTo) {
        Assert.notNull(userTo, "userTo" + MUST_NOT_BE_NULL);
        User user = get(userTo.id());
        boolean userBxDetailsChanged = updateFromTo(user, userTo);
        if (userBxDetailsChanged) {
            messageSender.sendDateMessage(LocalDate.now(), userTo.id());
        }
    }

    @Transactional
    public void changePassword(long id, String password) {
        Assert.notNull(password, "password" + MUST_NOT_BE_NULL);
        User user = get(id);
        user.setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    public void changePassword(String email, String password) {
        Assert.notNull(password, "password" + MUST_NOT_BE_NULL);
        User user = getByEmail(email);
        user.setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    public void enable(String email) {
        User user = getByEmail(email);
        user.setEnabled(true);
    }

    @Transactional
    public void enable(long id) {
        User user = get(id);
        user.setEnabled(true);
    }

    public void sendEmailVerify(String email) {
        User user = getByEmail(email);
        if (user.isEnabled()) {
            throw new EmailVerificationException("Email already verified:" + email);
        }
        messageSender.sendEmailVerifyMessage(email);
    }

    public void resetPassword(String email) {
        getByEmail(email);
        messageSender.sendPasswordResetMessage(email);
    }

    //use only for tests
    void setMessageSender(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}