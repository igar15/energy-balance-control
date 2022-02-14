package ru.javaprojects.emailverificationservice.messaging;

import org.springframework.stereotype.Component;
import ru.javaprojects.emailverificationservice.service.EmailVerificationService;

@Component
public class MessageReceiver {
    private final EmailVerificationService service;

    public MessageReceiver(EmailVerificationService service) {
        this.service = service;
    }

    public void receiveEmailVerifyMessage() {
        //TODO: RECEIVE MESSAGE FROM QUEUE TO VERIFY USER EMAIL
        String email = "";
        service.sendVerificationEmail(email);
    }
}
