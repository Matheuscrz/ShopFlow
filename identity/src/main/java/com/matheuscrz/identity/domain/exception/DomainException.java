package com.matheuscrz.identity.domain.exception;

/** Base das violações de regra do domínio de identidade. */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}