package com.matheuscrz.identity.application.service;

import com.matheuscrz.identity.application.port.in.ProcessNotificationUseCase;
import com.matheuscrz.identity.application.port.out.EmailSenderPort;
import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.PasswordResetRequestedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import com.matheuscrz.identity.domain.event.UserAddressChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class NotificationApplicationService implements ProcessNotificationUseCase {

    private final EmailSenderPort emailSenderPort;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void handleUserRegistered(UserRegisteredEvent event) {
        emailSenderPort.sendWelcomeEmail(event.email(), event.fullName());
    }

    @Override
    public void handlePasswordChanged(PasswordChangedEvent event) {
        emailSenderPort.sendPasswordChangedEmail(event.email(), "Usuário");
    }

    @Override
    public void handlePasswordResetRequested(PasswordResetRequestedEvent event) {
        String resetLink = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .path("/reset-password")
                .queryParam("token", event.resetToken())
                .build()
                .encode()
                .toUriString();

        emailSenderPort.sendPasswordResetEmail(
                event.email(),
                "Usuário",
                resetLink);
    }

    @Override
    public void handleAddressChanged(UserAddressChangedEvent event) {
        emailSenderPort.sendAddressChangedEmail(
                event.email(),
                "Usuário",
                event.action());
    }
}