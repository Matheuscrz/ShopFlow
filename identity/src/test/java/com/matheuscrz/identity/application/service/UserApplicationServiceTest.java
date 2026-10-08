package com.matheuscrz.identity.application.service;

import com.matheuscrz.identity.application.port.in.ManageUserUseCase.RegisterUserCommand;
import com.matheuscrz.identity.application.port.out.PasswordSecurityPort;
import com.matheuscrz.identity.application.port.out.UserNotificationEventPort;
import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import com.matheuscrz.identity.domain.exception.EmailAlreadyExistsException;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.Role;
import com.matheuscrz.identity.domain.model.User;
import com.matheuscrz.identity.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordSecurityPort passwordSecurity;

    @Mock
    private UserNotificationEventPort notificationEventPort;

    private final Clock clock = Clock.fixed(Instant.parse("2026-10-08T12:00:00Z"), ZoneOffset.UTC);

    private UserApplicationService userService;

    @BeforeEach
    void setUp() {
        this.userService = new UserApplicationService(
                userRepository,
                passwordSecurity,
                notificationEventPort,
                clock
        );
    }

    @Test
    @DisplayName("Deve registrar cliente, gerar hash e publicar evento de integração")
    void shouldRegisterNewUserSuccessfully() {
        var command = new RegisterUserCommand("novo.dev@shopflow.dev", "SenhaForte#2026", "Matheus Lima");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordSecurity.encode("SenhaForte#2026")).thenReturn("$argon2id$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User registered = userService.register(command);

        assertThat(registered.email().value()).isEqualTo("novo.dev@shopflow.dev");
        assertThat(registered.name()).isEqualTo("Matheus Lima");
        assertThat(registered.role()).isEqualTo(Role.CUSTOMER);
        assertThat(registered.status()).isEqualTo(UserStatus.ACTIVE);

        ArgumentCaptor<UserRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(notificationEventPort).publishUserRegistered(eventCaptor.capture());
        assertThat(eventCaptor.getValue().email()).isEqualTo("novo.dev@shopflow.dev");
    }

    @Test
    @DisplayName("Deve lançar EmailAlreadyExistsException quando e-mail já estiver em uso")
    void shouldThrowWhenEmailAlreadyRegistered() {
        var command = new RegisterUserCommand("existente@shopflow.dev", "SenhaForte#2026", "User");
        when(userRepository.existsByEmail(Email.of("existente@shopflow.dev"))).thenReturn(true);

        assertThatThrownBy(() -> userService.register(command))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
        verify(notificationEventPort, never()).publishUserRegistered(any());
    }
}