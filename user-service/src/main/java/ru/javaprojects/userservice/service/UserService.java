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
import ru.javaprojects.userservice.to.UserTo;
import ru.javaprojects.userservice.util.exception.EmailVerificationException;
import ru.javaprojects.userservice.util.exception.NotFoundException;

import java.time.LocalDate;

import static ru.javaprojects.userservice.util.UserUtil.prepareToSave;
import static ru.javaprojects.userservice.util.UserUtil.updateFromTo;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private MessageSender messageSender;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, MessageSender messageSender) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.messageSender = messageSender;
    }

    public User create(User user) {
        Assert.notNull(user, "user must not be null");
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

    public Page<User> getPage(Pageable pageable) {
        Assert.notNull(pageable, "pageable must not be null");
        return repository.findAllByOrderByNameAscEmail(pageable);
    }

    public User getByEmail(String email) {
        Assert.notNull(email, "email must not be null");
        return repository.findByEmail(email).orElseThrow(() -> new NotFoundException("Not found user with email=" + email));
    }

    public Page<User> getPageByKeyword(String keyword, Pageable pageable) {
        Assert.notNull(keyword, "keyword must not be null");
        Assert.notNull(pageable, "pageable must not be null");
        return repository.findAllByNameContainsIgnoreCaseOrEmailContainsIgnoreCaseOrderByNameAscEmail(keyword, keyword, pageable);
    }

    public void delete(int id) {
        User user = get(id);
        repository.delete(user);
        messageSender.sendUserDeletedMessage(id);
    }

    @Transactional
    public void update(UserTo userTo) {
        Assert.notNull(userTo, "userTo must not be null");
        User user = get(userTo.id());
        boolean userBxDetailsChanged = updateFromTo(user, userTo);
        if (userBxDetailsChanged) {
            messageSender.sendDateMessage(LocalDate.now(), userTo.id());
        }
    }

    @Transactional
    public void changePassword(int id, String password) {
        Assert.notNull(password, "password must not be null");
        User user = get(id);
        user.setPassword(passwordEncoder.encode(password));
    }

    @Transactional
    public void enable(String email) {
        User user = getByEmail(email);
        user.setEnabled(true);
    }

    public void resendEmailVerify(String email) {
        User user = getByEmail(email);
        if (user.isEnabled()) {
            throw new EmailVerificationException("Email already verified:" + email);
        }
        messageSender.sendEmailVerifyMessage(email);
    }
}