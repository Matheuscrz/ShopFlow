package com.matheuscrz.identity.application.port.in;

import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;

public interface ProcessNotificationUseCase {
    void handleUserRegistered(UserRegisteredEvent event);

    void handlePasswordChanged(PasswordChangedEvent event);
}