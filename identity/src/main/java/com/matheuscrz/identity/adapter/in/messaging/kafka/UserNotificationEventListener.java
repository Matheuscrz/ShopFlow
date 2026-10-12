package com.matheuscrz.identity.adapter.in.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheuscrz.identity.application.port.in.ProcessNotificationUseCase;
import com.matheuscrz.identity.application.port.out.EventDeduplicationPort;
import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserNotificationEventListener {

    private static final String EVENT_TYPE_HEADER = "eventType";

    private final EventDeduplicationPort deduplicationPort;
    private final ObjectMapper objectMapper;
    private final ProcessNotificationUseCase notificationUseCase;

    @KafkaListener(topics = "${app.kafka.topics.user-events}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(ConsumerRecord<String, String> record) {
        String eventIdStr = resolveEventId(record);
        String eventType = resolveEventType(record);
        String payload = record.value();

        UUID eventId;
        try {
            eventId = UUID.fromString(eventIdStr);
        } catch (IllegalArgumentException e) {
            log.error("Formato inválido de UUID no eventId: {}", eventIdStr);
            throw new IllegalStateException("ID do evento inválido", e);
        }

        if (deduplicationPort.isAlreadyProcessed(eventId)) {
            log.info("Evento duplicado ignorado: eventId={}, type={}", eventId, eventType);
            return;
        }

        try {
            dispatchTypedEvent(eventType, payload, eventIdStr);
            deduplicationPort.markAsProcessed(eventId, eventType);
            log.info("Evento processado com sucesso: eventId={}, type={}", eventId, eventType);
        } catch (JsonProcessingException e) {
            log.error(
                    "Payload inválido: eventId={}, type={}, payload={}",
                    eventId,
                    eventType,
                    payload,
                    e);
            throw new IllegalStateException("Payload de evento inválido", e);
        } catch (RuntimeException e) {
            log.error(
                    "Falha ao processar evento: eventId={}, type={}",
                    eventId,
                    eventType,
                    e);
            throw e;
        }
    }

    private void dispatchTypedEvent(
            String eventType,
            String jsonPayload,
            String eventId) throws JsonProcessingException {
        switch (eventType) {
            case "USER_REGISTERED" -> {
                UserRegisteredEvent event = objectMapper.readValue(jsonPayload, UserRegisteredEvent.class);
                log.info(
                        "Enviando e-mail de boas-vindas: eventId={}, email={}",
                        eventId,
                        event.email());
                notificationUseCase.handleUserRegistered(event);
            }
            case "PASSWORD_CHANGED" -> {
                PasswordChangedEvent event = objectMapper.readValue(jsonPayload, PasswordChangedEvent.class);
                log.info(
                        "Enviando e-mail de alteração de senha: eventId={}, userId={}",
                        eventId,
                        event.userId());
                notificationUseCase.handlePasswordChanged(event);
            }
            default -> log.debug(
                    "Evento sem notificação configurada: eventId={}, type={}",
                    eventId,
                    eventType);
        }
    }

    private String resolveEventType(ConsumerRecord<String, String> record) {
        var header = record.headers().lastHeader(EVENT_TYPE_HEADER);
        if (header == null || header.value() == null) {
            throw new IllegalStateException(
                    "Header obrigatório ausente: " + EVENT_TYPE_HEADER);
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }

    private String resolveEventId(ConsumerRecord<String, String> record) {
        if (record.key() == null || record.key().isBlank()) {
            throw new IllegalStateException("Evento Kafka sem chave/idempotency key");
        }
        return record.key();
    }
}