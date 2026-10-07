package com.matheuscrz.identity.domain.event;

import java.time.Instant;
import java.util.UUID;

public record UserAddressChangedEvent(
        UUID eventId,
        UUID userId,
        UUID addressId,
        String action,
        Instant occurredAt) {

    public static UserAddressChangedEvent of(
            UUID userId,
            UUID addressId,
            String action,
            Instant occurredAt) {
        return new UserAddressChangedEvent(
                UUID.randomUUID(), userId, addressId, action, occurredAt);
    }
}