package com.matheuscrz.identity.domain.exception;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("Credenciais inválidas");
    }

}
