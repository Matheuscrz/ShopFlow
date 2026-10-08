package com.matheuscrz.identity.application.port.out;

import java.util.UUID;

public interface EventDeduplicationPort {
    boolean isAlreadyProcessed(UUID eventId);
    void markAsProcessed(UUID eventId, String eventType);
}