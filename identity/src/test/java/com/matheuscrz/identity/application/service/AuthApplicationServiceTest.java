package com.matheuscrz.identity.application.service;

import com.matheuscrz.identity.application.port.in.AuthenticateUserUseCase.LoginCommand;
import com.matheuscrz.identity.application.port.out.PasswordSecurityPort;
import com.matheuscrz.identity.application.port.out.RefreshTokenRepositoryPort;
import com.matheuscrz.identity.application.port.out.TokenProviderPort;
import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.domain.exception.InvalidCredentialsException;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.Role;
import com.matheuscrz.identity.domain.model.User;
import com.matheuscrz.identity.domain.model.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class AuthApplicationServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private TokenProviderPort tokenProvider;

    @Mock
    private PasswordSecurityPort passwordSecurity;

    private final Clock clock = Clock.fixed(Instant.parse("2026-10-07T12:00:00Z"), ZoneOffset.UTC);

    private AuthApplicationService authService;

    @BeforeEach
    void setUp() {
        this.authService = new AuthApplicationService(
                userRepository,
                refreshTokenRepository,
                tokenProvider,
                passwordSecurity,
                clock
        );
    }

    @Test
    @DisplayName("Deve autenticar com sucesso e emitir tokens para credenciais válidas")
    void shouldAuthenticateSuccessfully() {
        UUID userId = UUID.randomUUID();
        Email email = Email.of("cliente@shopflow.dev");
        User user = User.restore(
                userId,
                email,
                "hashedPass",
                "Cliente Teste",
                Role.CUSTOMER,
                UserStatus.ACTIVE,
                clock.instant()
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordSecurity.matches("SenhaSegura123", "hashedPass")).thenReturn(true);
        when(tokenProvider.generateRawRefreshToken()).thenReturn("raw-refresh-uuid");
        when(tokenProvider.hashToken("raw-refresh-uuid")).thenReturn("hashed-refresh-uuid");
        when(tokenProvider.generateAccessToken(user)).thenReturn("access.token.jwt");

        var response = authService.login(new LoginCommand("cliente@shopflow.dev", "SenhaSegura123"));

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("access.token.jwt");
        assertThat(response.refreshToken()).isEqualTo("raw-refresh-uuid");

        verify(refreshTokenRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar InvalidCredentialsException quando utilizador não existir")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginCommand("desconhecido@shopflow.dev", "senha")))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(tokenProvider, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("Deve lançar InvalidCredentialsException quando a senha não corresponder")
    void shouldThrowWhenPasswordDoesNotMatch() {
        User user = User.restore(
                UUID.randomUUID(),
                Email.of("cliente@shopflow.dev"),
                "hashedPass",
                "Cliente Teste",
                Role.CUSTOMER,
                UserStatus.ACTIVE,
                clock.instant()
        );

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordSecurity.matches("senha-errada", "hashedPass")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginCommand("cliente@shopflow.dev", "senha-errada")))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(tokenProvider, never()).generateAccessToken(any());
    }
}