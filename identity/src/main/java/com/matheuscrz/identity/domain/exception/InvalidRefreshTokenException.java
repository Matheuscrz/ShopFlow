package com.matheuscrz.identity.domain.exception;

public class InvalidRefreshTokenException extends DomainException {

    public InvalidRefreshTokenException() {
        super("Refresh token inválido ou expirado.");
    }
}
