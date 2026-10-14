package com.matheuscrz.identity.domain.exception;

public class InvalidUserDataException extends DomainException {

    public InvalidUserDataException(String message) {
        super("Dados de usuário inválidos: " + message);
    }
}
