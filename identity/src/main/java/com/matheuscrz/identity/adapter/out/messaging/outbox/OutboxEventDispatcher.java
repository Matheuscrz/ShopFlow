package com.matheuscrz.identity.adapter.out.messaging.outbox;

import com.matheuscrz.identity.adapter.out.persistence.outbox.OutboxEventJpaEntity;
import com.matheuscrz.identity.adapter.out.persistence.outbox.OutboxEventStatus;
import com.matheuscrz.identity.adapter.out.persistence.outbox.SpringDataOutboxJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;
import java.time.Clock;

@Component
public class OutboxEventDispatcher {

    private static final Logger log = LoggerFactory.getLogger(OutboxEventDispatcher.class);

    private final SpringDataOutboxJpaRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Clock clock;
    private final String topic;

    public OutboxEventDispatcher(
            SpringDataOutboxJpaRepository outboxRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            Clock clock,
            @Value("${app.kafka.topics.user-events}") String topic) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.clock = clock;
        this.topic = topic;
    }

    /**
     * Executa imediatamente APÓS o commit no PostgreSQL com sucesso.
     * Propagation.REQUIRES_NEW abre uma nova transação rápida apenas para marcar o
     * status.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onEventCommitted(UUID outboxId) {
        outboxRepository.findById(outboxId).ifPresent(this::publishRecord);
    }

    /**
     * Poller de segurança a cada 10 segundos para mensagens PENDING (caso a
     * aplicação
     * tenha caído antes de executar o listener AFTER_COMMIT).
     */
    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void pollPendingOutboxEvents() {
        var pendings = outboxRepository.findByStatusOrderByCreatedAtAsc(
                OutboxEventStatus.PENDING,
                PageRequest.of(0, 50));

        for (OutboxEventJpaEntity event : pendings) {
            publishRecord(event);
        }
    }

    private void publishRecord(OutboxEventJpaEntity record) {
        kafkaTemplate.send(topic, record.getAggregateId(), record.getPayload())
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Falha ao entregar evento Outbox {} no Kafka", record.getId(), ex);
                    } else {
                        record.markPublished(clock);
                        outboxRepository.save(record);
                        log.info("Evento Outbox {} publicado no Kafka [offset: {}]",
                                record.getId(), result.getRecordMetadata().offset());
                    }
                });
    }
}