package com.matheuscrz.identity.adapter.out.security;

import com.matheuscrz.identity.domain.model.Email;
import com.matheuscrz.identity.domain.model.Role;
import com.matheuscrz.identity.domain.model.User;
import com.matheuscrz.identity.domain.model.UserStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderAdapterTest {

    private JwtTokenProviderAdapter jwtAdapter;
    private final String secret = "1234567890123456789012345678901234567890123456789012345678901234";
    private final Clock fixedClock = Clock.fixed(Instant.parse("2026-10-07T12:00:00Z"), ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        this.jwtAdapter = new JwtTokenProviderAdapter(secret, fixedClock);
    }

    @Test
    @DisplayName("Deve emitir e validar token contendo subject e claims em HS256")
    void shouldGenerateAndValidateHs256Token() {
        UUID userId = UUID.randomUUID();
        User user = User.restore(
                userId,
                Email.of("cliente@shopflow.dev"),
                "hash",
                "Cliente Teste",
                Role.CUSTOMER,
                UserStatus.ACTIVE,
                fixedClock.instant()
        );

        String token = jwtAdapter.generateAccessToken(user);
        assertThat(token).isNotBlank();

        Claims claims = jwtAdapter.parseAndValidate(token);
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("email", String.class)).isEqualTo("cliente@shopflow.dev");
        assertThat(claims.get("role", String.class)).isEqualTo("CUSTOMER");
    }

    @Test
    @DisplayName("Deve rejeitar tokens adulterados ou malformados")
    void shouldRejectMalformedToken() {
        String tamperedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.adulterado.assinatura";

        assertThatThrownBy(() -> jwtAdapter.parseAndValidate(tamperedToken))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("Deve validar a geração de refresh token bruto e o seu hash SHA-256")
    void shouldGenerateAndHashRefreshToken() {
        String rawToken = jwtAdapter.generateRawRefreshToken();
        assertThat(rawToken).isNotBlank();

        String hash = jwtAdapter.hashToken(rawToken);
        assertThat(hash).hasSize(64);
    }
}