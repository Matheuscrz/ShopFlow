package com.matheuscrz.identity.application.port.out;

import com.matheuscrz.identity.domain.model.User;

public interface TokenProviderPort {
    String generateAccessToken(User user);
    String generateRawRefreshToken();
    String hashToken(String rawToken);
}
