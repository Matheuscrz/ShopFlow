package com.matheuscrz.identity.application.port.in;

import com.matheuscrz.identity.domain.model.User;
import java.util.UUID;

public interface ManageUserUseCase {

    record RegisterUserCommand(
            String email,
            String rawPassword,
            String name
    ) {}

    record UpdateUserCommand(
            UUID userId,
            String name
    ) {}

    record ChangePasswordCommand(
            UUID userId,
            String currentPassword,
            String newPassword
    ) {}

    User register(RegisterUserCommand command);
    User update(UpdateUserCommand command);
    void changePassword(ChangePasswordCommand command);
    void disable(UUID userId);
    void enable(UUID userId);
    User getById(UUID userId);
}