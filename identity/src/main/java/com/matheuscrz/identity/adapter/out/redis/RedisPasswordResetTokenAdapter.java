package com.matheuscrz.identity.adapter.out.redis;

import com.matheuscrz.identity.application.port.out.PasswordResetTokenPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisPasswordResetTokenAdapter implements PasswordResetTokenPort {

    private static final String PREFIX = "pwd_reset:";
    private final StringRedisTemplate redisTemplate;

    @Override
    public void saveTokenHash(String tokenHash, UUID userId, Duration ttl) {
        redisTemplate.opsForValue().set(PREFIX + tokenHash, userId.toString(), ttl);
    }

    @Override
    public Optional<UUID> getUserIdByTokenHash(String tokenHash) {
        String userIdStr = redisTemplate.opsForValue().get(PREFIX + tokenHash);
        return Optional.ofNullable(userIdStr).map(UUID::fromString);
    }

    @Override
    public void invalidateTokenHash(String tokenHash) {
        redisTemplate.delete(PREFIX + tokenHash);
    }
}