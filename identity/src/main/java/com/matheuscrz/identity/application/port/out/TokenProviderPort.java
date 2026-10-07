package com.matheuscrz.identity.application.port.out;

import java.util.Map;

import com.matheuscrz.identity.domain.model.User;

public interface TokenProviderPort {
    String generateAccessToken(User user);
    String generateRawRefreshToken();
    String hashToken(String rawToken);
    Map<String, Object> validateAndExtractClaims(String token);
}
