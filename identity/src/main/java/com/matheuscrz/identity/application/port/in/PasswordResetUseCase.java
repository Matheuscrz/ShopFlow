package com.matheuscrz.identity.application.port.in;

public interface PasswordResetUseCase {
    void requestPasswordReset(String email);

    void resetPassword(String rawToken, String newRawPassword);
}