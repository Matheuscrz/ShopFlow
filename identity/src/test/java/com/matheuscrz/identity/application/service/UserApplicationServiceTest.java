package com.matheuscrz.identity.application.service;

import com.matheuscrz.identity.application.port.in.ManageUserUseCase.RegisterUserCommand;
import com.matheuscrz.identity.application.port.out.PasswordSecurityPort;
import com.matheuscrz.identity.application.port.out.SensitiveDataSecurityPort;
import com.matheuscrz.identity.application.port.out.UserNotificationEventPort;
import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.domain.event.UserRegisteredEvent;
import com.matheuscrz.identity.domain.exception.CpfAlreadyExistsException;
import com.matheuscrz.identity.domain.exception.EmailAlreadyExistsException;
import com.matheuscrz.identity.domain.exception.InvalidUserDataException;
import com.matheuscrz.identity.domain.model.Cpf;
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
import java.util.Optional;
import java.util.UUID;

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
        private SensitiveDataSecurityPort sensitiveDataSecurity;
        @Mock
        private UserNotificationEventPort notificationEventPort;

        private final Clock clock = Clock.fixed(Instant.parse("2026-10-08T12:00:00Z"), ZoneOffset.UTC);
        private UserApplicationService userService;

        @BeforeEach
        void setUp() {
                this.userService = new UserApplicationService(
                                userRepository,
                                passwordSecurity,
                                sensitiveDataSecurity,
                                notificationEventPort,
                                clock);
        }

        @Test
        @DisplayName("Deve registrar cliente com CPF com sucesso, criptografar e emitir evento")
        void shouldRegisterNewUserSuccessfully() {
                var command = new RegisterUserCommand(
                                "novo.dev@shopflow.dev",
                                "SenhaForte#2026",
                                "Matheus Lima",
                                "52998224725",
                                "11999998888");

                when(sensitiveDataSecurity.generateLookupHash("52998224725")).thenReturn("fake-lookup-hash-64-bytes");
                when(userRepository.findByCpfLookupHash("fake-lookup-hash-64-bytes")).thenReturn(Optional.empty());
                when(userRepository.existsByEmail(any())).thenReturn(false);
                when(passwordSecurity.encode("SenhaForte#2026")).thenReturn("$argon2id$hashedPassword");
                when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

                User registered = userService.register(command);

                assertThat(registered.email().value()).isEqualTo("novo.dev@shopflow.dev");
                assertThat(registered.name()).isEqualTo("Matheus Lima");
                assertThat(registered.cpf().value()).isEqualTo("52998224725");
                assertThat(registered.role()).isEqualTo(Role.CUSTOMER);
                assertThat(registered.status()).isEqualTo(UserStatus.ACTIVE);
                assertThat(registered.phone()).isEqualTo("11999998888");

                ArgumentCaptor<UserRegisteredEvent> eventCaptor = ArgumentCaptor.forClass(UserRegisteredEvent.class);
                verify(notificationEventPort).publishUserRegistered(eventCaptor.capture());
                assertThat(eventCaptor.getValue().email()).isEqualTo("novo.dev@shopflow.dev");
        }

        @Test
        @DisplayName("Deve lançar CpfAlreadyExistsException com e-mail mascarado quando CPF já estiver cadastrado")
        void shouldThrowCpfAlreadyExistsExceptionWhenCpfRegistered() {
                var command = new RegisterUserCommand(
                                "outro.dev@shopflow.dev",
                                "SenhaForte#2026",
                                "Outro Usuario",
                                "52998224725",
                                "11999998888");

                User existingUser = User.restore(
                                UUID.randomUUID(),
                                Email.of("matheus.cruz@shopflow.dev"),
                                "hash",
                                "Matheus Cruz",
                                new Cpf("52998224725"),
                                "11988887777",
                                Role.CUSTOMER,
                                UserStatus.ACTIVE,
                                clock.instant());

                when(sensitiveDataSecurity.generateLookupHash("52998224725")).thenReturn("fake-lookup-hash-64-bytes");
                when(userRepository.findByCpfLookupHash("fake-lookup-hash-64-bytes"))
                                .thenReturn(Optional.of(existingUser));

                assertThatThrownBy(() -> userService.register(command))
                                .isInstanceOf(CpfAlreadyExistsException.class)
                                .satisfies(ex -> {
                                        CpfAlreadyExistsException cpfEx = (CpfAlreadyExistsException) ex;
                                        assertThat(cpfEx.getMaskedEmail()).isEqualTo("m***@shopflow.dev");
                                });

                verify(userRepository, never()).existsByEmail(any());
                verify(userRepository, never()).save(any());
                verify(notificationEventPort, never()).publishUserRegistered(any());
        }

        @Test
        @DisplayName("Deve lançar EmailAlreadyExistsException quando e-mail já estiver em uso")
        void shouldThrowWhenEmailAlreadyRegistered() {
                var command = new RegisterUserCommand(
                                "existente@shopflow.dev",
                                "SenhaForte#2026",
                                "User",
                                "52998224725",
                                "11999998888");

                when(sensitiveDataSecurity.generateLookupHash("52998224725")).thenReturn("fake-lookup-hash-64-bytes");
                when(userRepository.findByCpfLookupHash("fake-lookup-hash-64-bytes")).thenReturn(Optional.empty());
                when(userRepository.existsByEmail(Email.of("existente@shopflow.dev"))).thenReturn(true);

                assertThatThrownBy(() -> userService.register(command))
                                .isInstanceOf(EmailAlreadyExistsException.class);

                verify(userRepository, never()).save(any());
                verify(notificationEventPort, never()).publishUserRegistered(any());
        }

        @Test
        @DisplayName("Deve falhar e não prosseguir quando o CPF for inválido no comando")
        void shouldThrowWhenCpfIsInvalid() {
                var command = new RegisterUserCommand(
                                "novo@shopflow.dev",
                                "SenhaForte#2026",
                                "User",
                                "11111111111",
                                "11999998888");

                assertThatThrownBy(() -> userService.register(command))
                                .isInstanceOf(InvalidUserDataException.class)
                                .hasMessageContaining("CPF inválido ou em formato incorreto.");

                verifyNoInteractions(sensitiveDataSecurity);
                verifyNoInteractions(userRepository);
                verifyNoInteractions(notificationEventPort);
        }

        @Test
        @DisplayName("Deve rejeitar telefone nulo")
        void shouldRejectNullPhone() {
                var command = new RegisterUserCommand(
                                "novo@shopflow.dev",
                                "SenhaForte#2026",
                                "User",
                                "52998224725",
                                null);

                assertThatThrownBy(() -> userService.register(command))
                                .isInstanceOf(InvalidUserDataException.class)
                                .hasMessageContaining("O telefone é obrigatório.");

                verifyNoInteractions(sensitiveDataSecurity);
                verifyNoInteractions(userRepository);
        }
}