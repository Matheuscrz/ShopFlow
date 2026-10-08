package com.matheuscrz.identity.adapter.out.persistence.outbox;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "outbox_events")
@Getter
public class OutboxEventJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, length = 100)
    private String aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 100)
    private OutboxEventType eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "JSONB")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxEventStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    protected OutboxEventJpaEntity() {
    }

    public OutboxEventJpaEntity(
            UUID id,
            String aggregateType,
            String aggregateId,
            OutboxEventType eventType,
            String payload,
            OutboxEventStatus status,
            Instant createdAt,
            Instant processedAt) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    public void markPublished() {
        this.status = OutboxEventStatus.PUBLISHED;
        this.processedAt = Instant.now();
    }
}
