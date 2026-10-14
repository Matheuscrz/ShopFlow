package com.matheuscrz.identity.application.port.out;

import java.time.Instant;
import java.util.UUID;

public interface TokenBlacklistPort {
    void blacklistToken(String jti, Instant expiresAt);

    void revokeAllForUser(UUID userId);

    boolean isBlacklisted(String jti);

    boolean isUserRevoked(UUID userId, Instant issuedAt);
}