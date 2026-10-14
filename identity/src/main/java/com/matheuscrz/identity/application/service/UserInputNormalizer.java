package com.matheuscrz.identity.application.service;

import java.util.Locale;

import com.matheuscrz.identity.domain.exception.InvalidUserDataException;

public final class UserInputNormalizer {

    private UserInputNormalizer() {
    }

    public static String email(String value) {
        return value == null ? null : value.strip().toLowerCase(Locale.ROOT);
    }

    public static String name(String value) {
        return collapse(value);
    }

    public static String text(String value) {
        return value == null ? null : value.strip();
    }

    public static String zipCode(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }

    private static String collapse(String value) {
        return value == null ? null : value.strip().replaceAll("\\s+", " ");
    }

    public static String sanitizePassword(String password) {
        if (password == null) {
            throw new InvalidUserDataException("Password cannot be null");
        }
        if (password.contains("\0")) {
            throw new InvalidUserDataException("Password contains illegal characters");
        }
        return password;
    }
}