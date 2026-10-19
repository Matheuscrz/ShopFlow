package com.matheuscrz.identity.application.service;

import com.matheuscrz.identity.application.port.in.AuthenticateUserUseCase;
import com.matheuscrz.identity.application.port.out.PasswordSecurityPort;
import com.matheuscrz.identity.application.port.out.RefreshTokenRepositoryPort;
import com.matheuscrz.identity.application.port.out.TokenProviderPort;
import com.matheuscrz.identity.application.port.out.UserRepositoryPort;
import com.matheuscrz.identity.domain.exception.InvalidCredentialsException;
import com.matheuscrz.identity.domain.exception.InvalidRefreshTokenException;
import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.RefreshToken;
import com.matheuscrz.identity.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthApplicationService implements AuthenticateUserUseCase {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final TokenProviderPort tokenProvider;
    private final PasswordSecurityPort passwordSecurity;
    private final Clock clock;

    public AuthApplicationService(
            UserRepositoryPort userRepository,
            RefreshTokenRepositoryPort refreshTokenRepository,
            TokenProviderPort tokenProvider,
            PasswordSecurityPort passwordSecurity,
            Clock clock) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
        this.passwordSecurity = passwordSecurity;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AuthTokens login(LoginCommand command) {
        String normalizedEmail = UserInputNormalizer.email(command.email());
        String sanitizedPassword = UserInputNormalizer.sanitizePassword(command.rawPassword());
        Email email = Email.of(normalizedEmail);

        log.info("Tentativa de login recebida. emailMasked={}", email.masked());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Falha de login: usuário não encontrado. emailMasked={}", email.masked());
                    return new InvalidCredentialsException();
                });

        if (!user.canAuthenticate() || !passwordSecurity.matches(sanitizedPassword, user.passwordHash())) {
            log.warn("Falha de login: credenciais inválidas. userId={}", user.id());
            throw new InvalidCredentialsException();
        }

        Instant now = clock.instant();
        String rawRefreshToken = tokenProvider.generateRawRefreshToken();
        String tokenHash = tokenProvider.hashToken(rawRefreshToken);

        UUID familyId = UUID.randomUUID();
        RefreshToken refreshToken = RefreshToken.issue(
                UUID.randomUUID(),
                user.id(),
                tokenHash,
                familyId,
                now,
                REFRESH_TOKEN_TTL);

        refreshTokenRepository.save(refreshToken);
        String accessToken = tokenProvider.generateAccessToken(user);

        log.info("Login realizado com sucesso. userId={}, familyId={}", user.id(), familyId);
        return new AuthTokens(accessToken, rawRefreshToken);
    }

    @Override
    @Transactional
    public AuthTokens refreshToken(RefreshCommand command) {
        Instant now = clock.instant();
        String hash = tokenProvider.hashToken(command.refreshToken().strip());
        RefreshToken currentToken = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (currentToken.isReuseAttempt()) {
            log.error(
                    "Reuso de refresh token detectado. userId={}, familyId={}",
                    currentToken.userId(),
                    currentToken.familyId());

            refreshTokenRepository.revokeFamilyOnReuseDetection(currentToken.familyId());
            throw new InvalidRefreshTokenException();
        }

        currentToken.assertUsable(now);
        User user = userRepository.findById(currentToken.userId())
                .filter(User::canAuthenticate)
                .orElseThrow(InvalidRefreshTokenException::new);

        String newRawToken = tokenProvider.generateRawRefreshToken();
        String newHash = tokenProvider.hashToken(newRawToken);
        UUID newId = UUID.randomUUID();

        RefreshToken newRefreshToken = RefreshToken.issue(
                newId,
                user.id(),
                newHash,
                currentToken.familyId(),
                now,
                REFRESH_TOKEN_TTL);

        currentToken.markUsed(newId, now);
        refreshTokenRepository.save(currentToken);
        refreshTokenRepository.save(newRefreshToken);

        String newAccessToken = tokenProvider.generateAccessToken(user);

        log.info(
                "Refresh token rotacionado com sucesso. userId={}, familyId={}",
                user.id(),
                currentToken.familyId());

        return new AuthTokens(newAccessToken, newRawToken);
    }

    @Override
    @Transactional
    public void logout(RefreshCommand command) {
        if (command.refreshToken() == null || command.refreshToken().isBlank()) {
            return;
        }
        String hash = tokenProvider.hashToken(command.refreshToken().strip());
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            token.revoke();
            refreshTokenRepository.save(token);

            log.info(
                    "Sessão encerrada. userId={}, familyId={}",
                    token.userId(),
                    token.familyId());
        });
    }
}