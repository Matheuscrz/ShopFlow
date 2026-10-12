package com.matheuscrz.identity.adapter.in.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.matheuscrz.identity.application.port.in.ProcessNotificationUseCase;
import com.matheuscrz.identity.application.port.out.EventDeduplicationPort;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNotificationEventListenerTest {

    @Mock
    private EventDeduplicationPort deduplicationPort;

    @Mock
    private ProcessNotificationUseCase notificationUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private UserNotificationEventListener listener;

    @BeforeEach
    void setUp() {
        this.listener = new UserNotificationEventListener(deduplicationPort, objectMapper, notificationUseCase);
    }

    @Test
    @DisplayName("Deve processar UserRegisteredEvent inédito e marcar como lido")
    void shouldProcessFreshUserRegisteredEvent() throws Exception {
        UUID eventId = UUID.randomUUID();
        UserRegisteredEvent event = UserRegisteredEvent.of(UUID.randomUUID(), "teste@shopflow.dev", "Test User");
        String payload = objectMapper.writeValueAsString(event);

        ConsumerRecord<String, String> record = buildRecord(eventId, "USER_REGISTERED", payload);

        when(deduplicationPort.isAlreadyProcessed(eventId)).thenReturn(false);

        listener.onMessage(record);

        ArgumentCaptor<UserRegisteredEvent> captor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(notificationUseCase).handleUserRegistered(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("teste@shopflow.dev");
        
        verify(deduplicationPort).markAsProcessed(eventId, "USER_REGISTERED");
    }

    @Test
    @DisplayName("Deve ignorar eventos que já foram consumidos (idempotência Kafka)")
    void shouldIgnoreAlreadyProcessedEvents() {
        UUID eventId = UUID.randomUUID();
        ConsumerRecord<String, String> record = buildRecord(eventId, "PASSWORD_CHANGED", "{}");

        when(deduplicationPort.isAlreadyProcessed(eventId)).thenReturn(true);

        listener.onMessage(record);

        verify(notificationUseCase, never()).handlePasswordChanged(any());
        verify(deduplicationPort, never()).markAsProcessed(any(UUID.class), any(String.class));
    }

    @Test
    @DisplayName("Deve falhar e disparar erro em caso de Payload inválido")
    void shouldThrowOnInvalidJson() {
        UUID eventId = UUID.randomUUID();
        ConsumerRecord<String, String> record = buildRecord(eventId, "USER_REGISTERED", "invalid-json");

        when(deduplicationPort.isAlreadyProcessed(eventId)).thenReturn(false);

        assertThatThrownBy(() -> listener.onMessage(record))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Payload de evento inválido");
    }

    private ConsumerRecord<String, String> buildRecord(UUID eventId, String type, String payload) {
        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                "identity.user-events", 
                0, 
                0L, 
                eventId.toString(), 
                payload
        );
        record.headers().add("eventType", type.getBytes(StandardCharsets.UTF_8));
        return record;
    }
}