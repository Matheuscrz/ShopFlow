package com.matheuscrz.identity.application.port.out;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenPort {
    void saveTokenHash(String tokenHash, UUID userId, Duration ttl);

    Optional<UUID> getUserIdByTokenHash(String tokenHash);

    void invalidateTokenHash(String tokenHash);
}