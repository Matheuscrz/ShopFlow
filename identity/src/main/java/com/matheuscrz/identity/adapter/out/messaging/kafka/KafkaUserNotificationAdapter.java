package com.matheuscrz.identity.adapter.out.messaging.kafka;

import com.matheuscrz.identity.application.port.out.UserNotificationEventPort;
import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.PasswordResetRequestedEvent;
import com.matheuscrz.identity.domain.event.UserAddressChangedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import com.matheuscrz.identity.domain.event.UserUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaUserNotificationAdapter implements UserNotificationEventPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaUserNotificationAdapter.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String userEventsTopic;

    public KafkaUserNotificationAdapter(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.user-events}") String userEventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.userEventsTopic = userEventsTopic;
    }

    @Override
    public void publishUserRegistered(UserRegisteredEvent event) {
        String key = event.userId().toString();
        kafkaTemplate.send(userEventsTopic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Falha ao publicar UserRegisteredEvent para userId: {}", key, ex);
                    } else {
                        log.info("UserRegisteredEvent publicado com sucesso no tópico {} [offset: {}]",
                                userEventsTopic, result.getRecordMetadata().offset());
                    }
                });
    }

    @Override
    public void publishPasswordResetRequested(PasswordResetRequestedEvent event) {
        String key = event.userId().toString();
        kafkaTemplate.send(userEventsTopic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Falha ao publicar PasswordResetRequestedEvent para userId: {}", key, ex);
                    } else {
                        log.info("PasswordResetRequestedEvent publicado com sucesso no tópico {} [offset: {}]",
                                userEventsTopic, result.getRecordMetadata().offset());
                    }
                });
    }

    @Override
    public void publishPasswordChanged(PasswordChangedEvent event) {
        send(event.userId().toString(), event);
    }

    @Override
    public void publishUserUpdated(UserUpdatedEvent event) {
        send(event.userId().toString(), event);
    }

    @Override
    public void publishAddressChanged(UserAddressChangedEvent event) {
        send(event.userId().toString(), event);
    }

    private void send(String key, Object event) {
        kafkaTemplate.send(userEventsTopic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Falha ao publicar notificação Kafka. key={}", key, ex);
                        return;
                    }

                    log.info(
                            "Notificação Kafka publicada. topic={}, key={}, offset={}",
                            userEventsTopic,
                            key,
                            result.getRecordMetadata().offset());
                });
    }
}
