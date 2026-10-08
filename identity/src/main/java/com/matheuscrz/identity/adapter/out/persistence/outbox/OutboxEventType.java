package com.matheuscrz.identity.adapter.out.persistence.outbox;

public enum OutboxEventType {
    USER_REGISTERED,
    PASSWORD_RESET_REQUESTED,
    PASSWORD_CHANGED,
    USER_UPDATED,
    USER_ADDRESS_CHANGED
}