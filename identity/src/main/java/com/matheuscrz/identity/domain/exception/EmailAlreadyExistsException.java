package com.matheuscrz.identity.domain.exception;

public class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException(String email) {
        super("Email já existe: " + email);
    }

}
