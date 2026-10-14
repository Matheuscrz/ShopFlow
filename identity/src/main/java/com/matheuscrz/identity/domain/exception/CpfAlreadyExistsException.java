package com.matheuscrz.identity.domain.exception;

public class CpfAlreadyExistsException extends DomainException {

    private final String maskedEmail;

    public CpfAlreadyExistsException(String maskedEmail) {
        super("CPF já cadastrado na plataforma.");
        this.maskedEmail = maskedEmail;
    }

    public String getMaskedEmail() {
        return maskedEmail;
    }
}