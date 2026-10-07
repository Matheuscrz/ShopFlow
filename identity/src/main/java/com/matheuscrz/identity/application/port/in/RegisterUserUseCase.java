package com.matheuscrz.identity.application.port.in;

import com.matheuscrz.identity.domain.model.User;

public interface RegisterUserUseCase {
    record Command(String email, String rawPassword, String name) {}
    User register(Command command);
}
