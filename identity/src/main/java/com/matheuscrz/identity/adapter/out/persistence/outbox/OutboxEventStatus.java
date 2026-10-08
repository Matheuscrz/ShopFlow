package com.matheuscrz.identity.adapter.out.persistence.outbox;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}