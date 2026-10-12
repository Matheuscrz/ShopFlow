package com.matheuscrz.identity.application.service;

import com.matheuscrz.identity.application.port.out.EmailSenderPort;
import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.UserAddressChangedEvent;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationApplicationServiceTest {

    @Mock
    private EmailSenderPort emailSenderPort;

    private NotificationApplicationService notificationService;

    @BeforeEach
    void setUp() {
        this.notificationService = new NotificationApplicationService(emailSenderPort);
    }

    @Test
    @DisplayName("Deve repassar evento de boas-vindas para o adaptador de email")
    void shouldForwardWelcomeEmail() {
        UUID userId = UUID.randomUUID();
        UserRegisteredEvent event = UserRegisteredEvent.of(userId, "cliente@shopflow.dev", "Cliente Teste");

        notificationService.handleUserRegistered(event);

        verify(emailSenderPort).sendWelcomeEmail("cliente@shopflow.dev", "Cliente Teste");
    }

    @Test
    @DisplayName("Deve repassar evento de senha alterada para o adaptador de email")
    void shouldForwardPasswordChangedEmail() {
        UUID userId = UUID.randomUUID();
        PasswordChangedEvent event = PasswordChangedEvent.of(userId, "seguranca@shopflow.dev", Instant.now());

        notificationService.handlePasswordChanged(event);

        verify(emailSenderPort).sendPasswordChangedEmail("seguranca@shopflow.dev", "Usuário");
    }

    @Test
    @DisplayName("Deve repassar evento de endereço alterado para o adaptador de email")
    void shouldForwardAddressChangedEmail() {
        UUID userId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        UserAddressChangedEvent event = UserAddressChangedEvent.of(
                userId,
                addressId,
                "cliente@shopflow.dev",
                "CREATED",
                Instant.now());

        notificationService.handleAddressChanged(event);

        verify(emailSenderPort).sendAddressChangedEmail(
                "cliente@shopflow.dev",
                "Usuário",
                "CREATED");
    }
}