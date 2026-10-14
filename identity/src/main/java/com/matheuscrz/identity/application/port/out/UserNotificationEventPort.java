package com.matheuscrz.identity.application.port.out;

import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.PasswordResetRequestedEvent;
import com.matheuscrz.identity.domain.event.UserAddressChangedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import com.matheuscrz.identity.domain.event.UserUpdatedEvent;

public interface UserNotificationEventPort {

    void publishUserRegistered(UserRegisteredEvent event);

    void publishPasswordResetRequested(PasswordResetRequestedEvent event);

    void publishPasswordChanged(PasswordChangedEvent event);

    void publishUserUpdated(UserUpdatedEvent event);

    void publishAddressChanged(UserAddressChangedEvent event);
}