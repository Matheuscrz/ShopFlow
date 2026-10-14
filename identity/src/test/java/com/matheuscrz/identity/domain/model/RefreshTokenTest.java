package com.matheuscrz.identity.domain.model;

import com.matheuscrz.identity.domain.exception.InvalidRefreshTokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenTest {

    private final Instant now = Instant.parse("2026-10-08T10:00:00Z");
    private final Duration ttl = Duration.ofDays(7);

    @Test
    @DisplayName("Deve emitir um refresh token ativo com data de expiração calculada corretamente")
    void shouldIssueActiveRefreshToken() {
        UUID userId = UUID.randomUUID();
        UUID familyId = UUID.randomUUID();

        RefreshToken token = RefreshToken.issue(
                UUID.randomUUID(), userId, "hash123", familyId, now, ttl
        );

        assertThat(token.status()).isEqualTo(RefreshTokenStatus.ACTIVE);
        assertThat(token.expiresAt()).isEqualTo(now.plus(ttl));
        assertThat(token.isExpired(now)).isFalse();
        assertThat(token.isReuseAttempt()).isFalse();
    }

    @Test
    @DisplayName("Deve marcar o token como USED e vincular o ID substituto")
    void shouldMarkTokenAsUsed() {
        RefreshToken token = RefreshToken.issue(
                UUID.randomUUID(), UUID.randomUUID(), "hash123", UUID.randomUUID(), now, ttl
        );
        UUID replacedById = UUID.randomUUID();

        token.markUsed(replacedById, now);

        assertThat(token.status()).isEqualTo(RefreshTokenStatus.USED);
        assertThat(token.replacedBy()).isEqualTo(replacedById);
        assertThat(token.isReuseAttempt()).isTrue();
    }

    @Test
    @DisplayName("Deve falhar ao tentar marcar como usado um token já expirado")
    void shouldThrowWhenTokenIsExpired() {
        RefreshToken token = RefreshToken.issue(
                UUID.randomUUID(), UUID.randomUUID(), "hash123", UUID.randomUUID(), now, ttl
        );
        Instant afterExpiration = now.plus(Duration.ofDays(8));

        assertThatThrownBy(() -> token.markUsed(UUID.randomUUID(), afterExpiration))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }
}