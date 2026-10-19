package com.matheuscrz.identity.adapter.out.security;

import com.matheuscrz.identity.application.port.out.TokenProviderPort;
import com.matheuscrz.identity.config.JwtProperties;
import com.matheuscrz.identity.config.RsaKeyProvider;
import com.matheuscrz.identity.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

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
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(15);
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final String kid;
    private final String issuer;
    private final String audience;
    private final Clock clock;
    private final SecureRandom secureRandom = new SecureRandom();

    public JwtTokenProviderAdapter(
            RsaKeyProvider rsaKeyProvider,
            JwtProperties jwtProperties,
            Clock clock) {
        this.privateKey = rsaKeyProvider.getPrivateKey();
        this.publicKey = rsaKeyProvider.getPublicKey();
        this.kid = rsaKeyProvider.getKid();
        this.issuer = jwtProperties.getIssuer();
        this.audience = jwtProperties.getAudience();
        this.clock = clock;
    }

    @Override
    public String generateAccessToken(User user) {
        Instant now = clock.instant();
        Instant expiry = now.plus(ACCESS_TOKEN_TTL);

        return Jwts.builder()
                .header()
                .keyId(kid)
                .and()
                .id(UUID.randomUUID().toString())
                .subject(user.id().toString())
                .issuer(issuer)
                .audience().add(audience).and()
                .claim("email", user.email().value())
                .claim("roles", List.of(user.role().name()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(privateKey, Jwts.SIG.RS256)
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

    @Override
    public Map<String, Object> validateAndExtractClaims(String token) {
        return parseAndValidate(token);
    }

    public Claims parseAndValidate(String token) {
        try {
            Jws<Claims> parsed = Jwts.parser()
                    .verifyWith(publicKey)
                    .clock(() -> Date.from(clock.instant()))
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .build()
                    .parseSignedClaims(token);

            if (!"RS256".equals(parsed.getHeader().getAlgorithm())
                    || !kid.equals(parsed.getHeader().getKeyId())) {
                throw new SignatureException("Algoritmo ou chave JWT não permitidos");
            }
            return parsed.getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            throw new SignatureException("JWT inválido ou algoritmo não permitido", ex);
        }
    }
}