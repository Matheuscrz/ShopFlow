package com.matheuscrz.identity.adapter.in.web.dto;

import com.matheuscrz.identity.application.port.in.AuthenticateUserUseCase.AuthTokens;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
    public static AuthResponse fromDomain(AuthTokens tokens) {
        return new AuthResponse(tokens.accessToken(), tokens.refreshToken());
    }
}