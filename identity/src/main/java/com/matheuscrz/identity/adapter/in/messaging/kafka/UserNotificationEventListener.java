package com.matheuscrz.identity.adapter.in.messaging.kafka;

import com.matheuscrz.identity.domain.event.PasswordResetRequestedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.UserAddressChangedEvent;
import com.matheuscrz.identity.domain.event.UserUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(id = "user-notification-consumer", topics = "${app.kafka.topics.user-events}")
public class UserNotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserNotificationEventListener.class);

    @KafkaHandler
    public void handleUserRegistered(UserRegisteredEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("================ [SIMULAÇÃO EMAIL DE BOAS-VINDAS] ================");
        log.info("Para: {} <{}>", event.fullName(), event.email());
        log.info("Assunto: Bem-vindo ao ShopFlow!");
        log.info("Corpo: Olá, {}! Seu cadastro foi concluído com sucesso. ID: {}", event.fullName(), key);
        log.info("==================================================================");
    }

    @KafkaHandler
    public void handlePasswordReset(PasswordResetRequestedEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("================ [SIMULAÇÃO RECUPERAÇÃO DE SENHA] ================");
        log.info("Para: {}", event.email());
        log.info("Assunto: Recuperação de Acesso ShopFlow");
        log.info("Token de Redefinição: {}", event.resetToken());
        log.info("Válido até: {}", event.expiresAt());
        log.info("Link simulado: https://shopflow.com/reset-password?token={}", event.resetToken());
        log.info("==================================================================");
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object unknownEvent) {
        log.warn("Evento não reconhecido recebido no tópico: {}", unknownEvent);
    }

    @KafkaHandler
    public void handlePasswordChanged(PasswordChangedEvent event) {
        log.info("[SIMULAÇÃO EMAIL] Senha alterada para {}", event.email());
    }

    @KafkaHandler
    public void handleUserUpdated(UserUpdatedEvent event) {
        log.info("[SIMULAÇÃO EMAIL] Dados do usuário atualizados: {}", event.email());
    }

    @KafkaHandler
    public void handleAddressChanged(UserAddressChangedEvent event) {
        log.info(
                "[SIMULAÇÃO EMAIL] Endereço {} para userId={}, addressId={}",
                event.action(),
                event.userId(),
                event.addressId());
    }
}