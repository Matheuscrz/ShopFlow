package com.matheuscrz.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record PasswordResetRequestedEvent(
    UUID eventId,
    UUID userId,
    String email,
    String resetToken,
    Instant expiresAt,
    Instant occurredAt
) {
    public static PasswordResetRequestedEvent of(UUID userId, String email, String resetToken, Instant expiresAt) {
        return new PasswordResetRequestedEvent(UUID.randomUUID(), userId, email, resetToken, expiresAt, Instant.now());
    }
}
