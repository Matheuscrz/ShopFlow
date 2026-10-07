package com.matheuscrz.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record PasswordChangedEvent(
        UUID eventId,
        UUID userId,
        String email,
        String occurredAtMessage,
        Instant occurredAt) {

    public static PasswordChangedEvent of(UUID userId, String email, Instant occurredAt) {
        return new PasswordChangedEvent(
                UUID.randomUUID(),
                userId,
                email,
                "Senha alterada com sucesso.",
                occurredAt);
    }
}