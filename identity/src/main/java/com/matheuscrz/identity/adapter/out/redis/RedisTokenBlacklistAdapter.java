package com.matheuscrz.identity.adapter.out.redis;

import com.matheuscrz.identity.application.port.out.TokenBlacklistPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisTokenBlacklistAdapter implements TokenBlacklistPort {

    private static final String JTI_PREFIX = "blacklist:jti:";
    private static final String USER_PREFIX = "blacklist:user:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void blacklistToken(String jti, Instant expiresAt) {
        if (jti == null || jti.isBlank()) {
            return;
        }
        Duration ttl = Duration.between(Instant.now(), expiresAt);
        if (!ttl.isNegative() && !ttl.isZero()) {
            redisTemplate.opsForValue().set(JTI_PREFIX + jti, "1", ttl);
        }
    }

    @Override
    public void revokeAllForUser(UUID userId) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        // TTL de 8 dias para cobrir o ciclo máximo de vida do Refresh Token (7 dias)
        redisTemplate.opsForValue().set(USER_PREFIX + userId, timestamp, Duration.ofDays(8));
    }

    @Override
    public boolean isBlacklisted(String jti) {
        if (jti == null || jti.isBlank()) {
            return false;
        }
        return Boolean.TRUE.equals(redisTemplate.hasKey(JTI_PREFIX + jti));
    }

    @Override
    public boolean isUserRevoked(UUID userId, Instant issuedAt) {
        String revokedAtMillisStr = redisTemplate.opsForValue().get(USER_PREFIX + userId);
        if (revokedAtMillisStr == null) {
            return false;
        }
        long revokedAtMillis = Long.parseLong(revokedAtMillisStr);
        return issuedAt.toEpochMilli() < revokedAtMillis;
    }
}