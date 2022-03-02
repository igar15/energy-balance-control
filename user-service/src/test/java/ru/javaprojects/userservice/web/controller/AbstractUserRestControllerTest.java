package ru.javaprojects.userservice.web.controller;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.energybalancecontrolshared.test.AbstractControllerTest;
import ru.javaprojects.energybalancecontrolshared.web.security.JwtProvider;
import ru.javaprojects.userservice.messaging.MessageSender;
import ru.javaprojects.userservice.service.UserService;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Transactional
public abstract class AbstractUserRestControllerTest extends AbstractControllerTest {

    @Autowired
    protected UserService service;

    @Mock
    private MessageSender messageSender;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtProvider jwtProvider;

    @PostConstruct
    void setupUserService() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method messageSenderSetter = service.getClass().getDeclaredMethod("setMessageSender", MessageSender.class);
        messageSenderSetter.setAccessible(true);
        messageSenderSetter.invoke(service, messageSender);
    }
}