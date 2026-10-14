package com.matheuscrz.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
    UUID eventId,
    UUID userId,
    String email,
    String fullName,
    Instant occurredAt
) {
    public static UserRegisteredEvent of(UUID userId, String email, String fullName) {
        return new UserRegisteredEvent(UUID.randomUUID(), userId, email, fullName, Instant.now());
    }
}
