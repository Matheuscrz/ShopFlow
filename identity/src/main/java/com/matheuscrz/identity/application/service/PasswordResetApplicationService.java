package com.matheuscrz.identity.application.service;

import com.matheuscrz.identity.application.port.in.PasswordResetUseCase;
import com.matheuscrz.identity.application.port.out.PasswordResetTokenPort;
import com.matheuscrz.identity.application.port.out.PasswordSecurityPort;
import com.matheuscrz.identity.application.port.out.TokenProviderPort;
import com.matheuscrz.identity.application.port.out.UserNotificationEventPort;
import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.domain.event.PasswordChangedEvent;
import com.matheuscrz.identity.domain.event.PasswordResetRequestedEvent;
import com.matheuscrz.identity.domain.exception.InvalidUserDataException;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetApplicationService implements PasswordResetUseCase {

    private final UserRepositoryPort userRepository;
    private final TokenProviderPort tokenProvider;
    private final PasswordResetTokenPort resetTokenPort;
    private final UserNotificationEventPort notificationEventPort;
    private final PasswordSecurityPort passwordSecurity;
    private final Clock clock;

    @Override
    @Transactional
    public void requestPasswordReset(String emailInput) {
        String normalizedEmail = UserInputNormalizer.email(emailInput);
        Optional<User> userOpt = userRepository.findByEmail(Email.of(normalizedEmail));

        // Retorna silenciosamente para evitar enumeração de e-mails
        if (userOpt.isEmpty() || !userOpt.get().canAuthenticate()) {
            return;
        }

        User user = userOpt.get();

        // Reutiliza o gerador seguro de bytes aleatórios e o algoritmo SHA-256
        String rawToken = tokenProvider.generateRawRefreshToken();
        String tokenHash = tokenProvider.hashToken(rawToken);

        Duration ttl = Duration.ofMinutes(15); // Token válido por apenas 15 minutos
        resetTokenPort.saveTokenHash(tokenHash, user.id(), ttl);

        notificationEventPort.publishPasswordResetRequested(
                PasswordResetRequestedEvent.of(user.id(), user.email().value(), rawToken, clock.instant().plus(ttl)));
    }

    @Override
    @Transactional
    public void resetPassword(String rawToken, String newRawPassword) {
        String tokenHash = tokenProvider.hashToken(rawToken.strip());

        UUID userId = resetTokenPort.getUserIdByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidUserDataException("Token de recuperação inválido ou expirado."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidUserDataException("Usuário não encontrado."));

        String sanitizedPassword = UserInputNormalizer.sanitizePassword(newRawPassword);
        user.changePasswordHash(passwordSecurity.encode(sanitizedPassword));
        userRepository.save(user);

        // Invalida o token para uso único
        resetTokenPort.invalidateTokenHash(tokenHash);

        // Dispara o evento genérico para notificar o usuário de que a senha mudou
        notificationEventPort.publishPasswordChanged(
                PasswordChangedEvent.of(user.id(), user.email().value(), clock.instant()));
    }
}