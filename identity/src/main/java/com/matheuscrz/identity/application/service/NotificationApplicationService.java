package com.matheuscrz.identity.application.service;

import com.matheuscrz.identity.application.port.in.ProcessNotificationUseCase;
import com.matheuscrz.identity.application.port.out.EmailSenderPort;
import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationApplicationService implements ProcessNotificationUseCase {

    private final EmailSenderPort emailSenderPort;

    @Override
    public void handleUserRegistered(UserRegisteredEvent event) {
        emailSenderPort.sendWelcomeEmail(event.email(), event.fullName());
    }

    @Override
    public void handlePasswordChanged(PasswordChangedEvent event) {
        emailSenderPort.sendPasswordChangedEmail(event.email(), "Usuário");
    }
}