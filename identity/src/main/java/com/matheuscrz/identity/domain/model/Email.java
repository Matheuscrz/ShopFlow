package com.matheuscrz.identity.domain.model;

import java.util.Locale;
import java.util.regex.Pattern;

import com.matheuscrz.identity.domain.exception.InvalidEmailException;

public record Email(String value) {

    private static final int MAX_LENGTH = 254;
    private static final Pattern FORMAT = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("O e-mail é obrigatório.");
        }
        value = value.strip().toLowerCase(Locale.ROOT);
        if (value.length() > MAX_LENGTH || !FORMAT.matcher(value).matches()) {
            throw new InvalidEmailException("E-mail inválido.");
        }
    }

    public static Email of(String raw) {
        return new Email(raw);
    }

    /** Ex.: m***@shopflow.dev */
    public String masked() {
        int at = value.indexOf('@');
        return value.charAt(0) + "***" + value.substring(at);
    }

    @Override
    public String toString() {
        return masked();
    }
}
