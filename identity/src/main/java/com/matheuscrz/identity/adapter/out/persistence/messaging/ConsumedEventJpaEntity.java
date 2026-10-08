package com.matheuscrz.identity.adapter.out.persistence.messaging;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "consumed_events")
@Getter
public class ConsumedEventJpaEntity {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt;

    protected ConsumedEventJpaEntity() {
    }

    public ConsumedEventJpaEntity(UUID eventId, String eventType, Instant receivedAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.receivedAt = receivedAt;
    }
}