package com.matheuscrz.identity.adapter.out.security;

import com.matheuscrz.identity.application.port.out.TokenProviderPort;
import com.matheuscrz.identity.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HexFormat;

@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(15);
    private final SecretKey secretKey;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    public JwtTokenProviderAdapter(
            @Value("${jwt.secret}") String secret,
            Clock clock) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.clock = clock;
    }

    @Override
    public String generateAccessToken(User user) {
        Instant now = clock.instant();
        Instant expiry = now.plus(ACCESS_TOKEN_TTL);

        return Jwts.builder()
                .subject(user.id().toString())
                .claim("email", user.email().value())
                .claim("role", user.role().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    @Override
    public String generateRawRefreshToken() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    @Override
    public String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash SHA-256 não disponível", e);
        }
    }
}