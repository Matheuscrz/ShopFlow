package com.matheuscrz.identity.application.port.in;

public interface AuthenticateUserUseCase {
    record LoginCommand(String email, String rawPassword) {}
    record RefreshCommand(String refreshToken) {}
    record AuthTokens(String accessToken, String refreshToken) {}

    AuthTokens login(LoginCommand command);
    AuthTokens refreshToken(RefreshCommand command);
    void logout(RefreshCommand command);
}
