package com.matheuscrz.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record UserUpdatedEvent(
        UUID eventId,
        UUID userId,
        String email,
        String name,
        Instant occurredAt) {

    public static UserUpdatedEvent of(
            UUID userId,
            String email,
            String name,
            Instant occurredAt) {
        return new UserUpdatedEvent(
                UUID.randomUUID(), userId, email, name, occurredAt);
    }
}