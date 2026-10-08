package com.matheuscrz.identity.adapter.out.redis;

import com.matheuscrz.identity.application.port.out.RateLimiterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisRateLimiterAdapter implements RateLimiterPort {

    private static final String PREFIX = "rl:";
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean tryAcquire(String route, String clientKey, int maxAttempts, Duration window) {
        String key = buildKey(route, clientKey);
        Long current = redisTemplate.opsForValue().increment(key);

        if (current != null && current == 1) {
            redisTemplate.expire(key, window);
        }

        return current != null && current <= maxAttempts;
    }

    @Override
    public long getRemainingWaitSeconds(String route, String clientKey) {
        String key = buildKey(route, clientKey);
        Long ttl = redisTemplate.getExpire(key);
        return (ttl != null && ttl > 0) ? ttl : 0;
    }

    private String buildKey(String route, String clientKey) {
        return PREFIX + route + ":" + clientKey;
    }
}