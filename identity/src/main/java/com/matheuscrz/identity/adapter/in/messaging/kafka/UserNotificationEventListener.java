package com.matheuscrz.identity.adapter.in.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matheuscrz.identity.application.port.out.EventDeduplicationPort;
import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.PasswordResetRequestedEvent;
import com.matheuscrz.identity.domain.event.UserAddressChangedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import com.matheuscrz.identity.domain.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserNotificationEventListener {

    private final EventDeduplicationPort deduplicationPort;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${app.kafka.topics.user-events}",
            groupId = "${spring.kafka.consumer.group-id:identity-service-group}"
    )
    @Transactional
    public void onMessage(ConsumerRecord<String, String> record) {
        Header eventIdHeader = record.headers().lastHeader("eventId");
        Header eventTypeHeader = record.headers().lastHeader("eventType");

        if (eventIdHeader == null || eventTypeHeader == null) {
            log.warn("Mensagem descartada no offset {}: headers eventId ou eventType ausentes", record.offset());
            return;
        }

        UUID eventId;
        try {
            eventId = UUID.fromString(new String(eventIdHeader.value(), StandardCharsets.UTF_8));
        } catch (IllegalArgumentException ex) {
            log.error("Formato inválido de UUID no header eventId: {}", new String(eventIdHeader.value(), StandardCharsets.UTF_8));
            return;
        }

        String eventType = new String(eventTypeHeader.value(), StandardCharsets.UTF_8);

        // Deduplicação no banco: evita reprocessamento por at-least-once delivery
        if (deduplicationPort.isAlreadyProcessed(eventId)) {
            log.info("Evento [id={}, tipo={}] já foi processado anteriormente. Ignorando.", eventId, eventType);
            return;
        }

        dispatchTypedEvent(eventType, record.value(), record.key());
        deduplicationPort.markAsProcessed(eventId, eventType);
    }

    private void dispatchTypedEvent(String eventType, String jsonPayload, String partitionKey) {
        try {
            switch (eventType) {
                case "USER_REGISTERED" -> {
                    UserRegisteredEvent event = objectMapper.readValue(jsonPayload, UserRegisteredEvent.class);
                    log.info("Processando registro de usuário: ID={}, Email={}", event.userId(), event.email());
                }
                case "PASSWORD_RESET_REQUESTED" -> {
                    PasswordResetRequestedEvent event = objectMapper.readValue(jsonPayload, PasswordResetRequestedEvent.class);
                    log.info("Processando solicitação de reset de senha para o email: {}", event.email());
                }
                case "PASSWORD_CHANGED" -> {
                    PasswordChangedEvent event = objectMapper.readValue(jsonPayload, PasswordChangedEvent.class);
                    log.info("Processando notificação de senha alterada: ID={}", event.userId());
                }
                case "USER_UPDATED" -> {
                    UserUpdatedEvent event = objectMapper.readValue(jsonPayload, UserUpdatedEvent.class);
                    log.info("Processando atualização cadastral: ID={}", event.userId());
                }
                case "USER_ADDRESS_CHANGED" -> {
                    UserAddressChangedEvent event = objectMapper.readValue(jsonPayload, UserAddressChangedEvent.class);
                    log.info("Processando alteração de endereço: Ação={}, AddressID={}", event.action(), event.addressId());
                }
                default -> log.warn("Tipo de evento desconhecido: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Erro na desserialização/processamento do evento {} para key={}: {}", eventType, partitionKey, e.getMessage());
            throw new IllegalStateException("Falha no processamento da mensagem Kafka", e);
        }
    }
}