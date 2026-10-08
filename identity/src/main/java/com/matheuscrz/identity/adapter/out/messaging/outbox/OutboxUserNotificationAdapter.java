package com.matheuscrz.identity.adapter.out.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheuscrz.identity.adapter.out.persistence.outbox.OutboxEventJpaEntity;
import com.matheuscrz.identity.adapter.out.persistence.outbox.OutboxEventStatus;
import com.matheuscrz.identity.adapter.out.persistence.outbox.OutboxEventType;
import com.matheuscrz.identity.adapter.out.persistence.outbox.SpringDataOutboxJpaRepository;
import com.matheuscrz.identity.application.port.out.UserNotificationEventPort;
import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.PasswordResetRequestedEvent;
import com.matheuscrz.identity.domain.event.UserAddressChangedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import com.matheuscrz.identity.domain.event.UserUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Primary
@Component
public class OutboxUserNotificationAdapter implements UserNotificationEventPort {

    private final SpringDataOutboxJpaRepository outboxRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper;

    public OutboxUserNotificationAdapter(
            SpringDataOutboxJpaRepository outboxRepository,
            ApplicationEventPublisher applicationEventPublisher,
            ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishUserRegistered(UserRegisteredEvent event) {
        saveAndEmit(
                "User",
                event.userId().toString(),
                OutboxEventType.USER_REGISTERED,
                event);
    }

    public void publishPasswordResetRequested(PasswordResetRequestedEvent event) {
        saveAndEmit(
                "User",
                event.userId().toString(),
                OutboxEventType.PASSWORD_RESET_REQUESTED,
                event);
    }

    public void publishPasswordChanged(PasswordChangedEvent event) {
        saveAndEmit(
                "User",
                event.userId().toString(),
                OutboxEventType.PASSWORD_CHANGED,
                event);
    }

    public void publishUserUpdated(UserUpdatedEvent event) {
        saveAndEmit(
                "User",
                event.userId().toString(),
                OutboxEventType.USER_UPDATED,
                event);
    }

    public void publishAddressChanged(UserAddressChangedEvent event) {
        saveAndEmit(
                "UserAddress",
                event.userId().toString(),
                OutboxEventType.USER_ADDRESS_CHANGED,
                event);
    }

    private void saveAndEmit(
            String aggregateType,
            String aggregateId,
            OutboxEventType eventType,
            Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            OutboxEventJpaEntity entity = new OutboxEventJpaEntity(
                    UUID.randomUUID(),
                    aggregateType,
                    aggregateId,
                    eventType,
                    jsonPayload,
                    OutboxEventStatus.PENDING,
                    Instant.now(),
                    null);

            outboxRepository.save(entity);
            applicationEventPublisher.publishEvent(entity.getId());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Falha ao serializar payload do Outbox", e);
        }
    }
}