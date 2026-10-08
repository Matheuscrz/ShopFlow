package com.matheuscrz.identity.adapter.out.redis;

import com.matheuscrz.identity.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RedisRateLimiterAdapterIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RedisRateLimiterAdapter rateLimiter;

    @Test
    @DisplayName("Deve permitir requisições até o limite configurado e bloquear quando excedido")
    void shouldEnforceRateLimitingOnRealRedis() {
        String route = "test-route";
        String clientKey = "127.0.0.1";
        int maxAttempts = 3;
        Duration window = Duration.ofSeconds(10);

        assertThat(rateLimiter.tryAcquire(route, clientKey, maxAttempts, window)).isTrue();
        assertThat(rateLimiter.tryAcquire(route, clientKey, maxAttempts, window)).isTrue();
        assertThat(rateLimiter.tryAcquire(route, clientKey, maxAttempts, window)).isTrue();

        // Limite atingido: deve recusar a 4ª tentativa
        assertThat(rateLimiter.tryAcquire(route, clientKey, maxAttempts, window)).isFalse();
        assertThat(rateLimiter.getRemainingWaitSeconds(route, clientKey)).isGreaterThan(0);
    }
}