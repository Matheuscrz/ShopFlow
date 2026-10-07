package com.matheuscrz.identity.application.service;

import java.util.Locale;

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
}