package com.matheuscrz.identity.adapter.in.web.dto;

import com.matheuscrz.identity.domain.model.User;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String name,
        String role,
        String status,
        Instant createdAt
) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.id(),
                user.email().value(),
                user.name(),
                user.role().name(),
                user.status().name(),
                user.createdAt()
        );
    }
}