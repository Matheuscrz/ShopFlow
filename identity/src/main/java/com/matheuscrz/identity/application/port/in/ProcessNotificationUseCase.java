package com.matheuscrz.identity.application.port.in;

import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.PasswordResetRequestedEvent;
import com.matheuscrz.identity.domain.event.UserAddressChangedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;

public interface ProcessNotificationUseCase {
    void handleUserRegistered(UserRegisteredEvent event);

    void handlePasswordChanged(PasswordChangedEvent event);

    void handlePasswordResetRequested(PasswordResetRequestedEvent event);

    void handleAddressChanged(UserAddressChangedEvent event);
}