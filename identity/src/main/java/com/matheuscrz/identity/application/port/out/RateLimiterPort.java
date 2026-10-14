package com.matheuscrz.identity.application.port.out;

import java.time.Duration;

public interface RateLimiterPort {
    boolean tryAcquire(String route, String clientKey, int maxAttempts, Duration window);
    long getRemainingWaitSeconds(String route, String clientKey);
}