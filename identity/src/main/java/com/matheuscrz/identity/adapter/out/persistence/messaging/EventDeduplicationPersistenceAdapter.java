package com.matheuscrz.identity.adapter.out.persistence.messaging;

import com.matheuscrz.identity.application.port.out.EventDeduplicationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventDeduplicationPersistenceAdapter implements EventDeduplicationPort {

    private final SpringDataConsumedEventJpaRepository repository;
    private final Clock clock;

    @Override
    public boolean isAlreadyProcessed(UUID eventId) {
        return repository.existsById(eventId);
    }

    @Override
    public void markAsProcessed(UUID eventId, String eventType) {
        repository.save(new ConsumedEventJpaEntity(eventId, eventType, Instant.now(clock)));
    }
}