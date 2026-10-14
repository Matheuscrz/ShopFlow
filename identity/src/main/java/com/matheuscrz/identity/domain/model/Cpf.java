package com.matheuscrz.identity.domain.model;

import com.matheuscrz.identity.domain.exception.InvalidUserDataException;

public record Cpf(String value) {

    public Cpf {
        if (value == null || !isValid(value)) {
            throw new InvalidUserDataException("CPF inválido ou em formato incorreto.");
        }
        value = clean(value);
    }

    public static String clean(String cpf) {
        return cpf.replaceAll("\\D", "");
    }

    public String formatted() {
        return value.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    private static boolean isValid(String raw) {
        String digits = clean(raw);
        if (digits.length() != 11 || digits.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int sum1 = 0;
            for (int i = 0; i < 9; i++) {
                sum1 += Character.getNumericValue(digits.charAt(i)) * (10 - i);
            }
            int check1 = 11 - (sum1 % 11);
            if (check1 >= 10)
                check1 = 0;
            if (check1 != Character.getNumericValue(digits.charAt(9)))
                return false;

            int sum2 = 0;
            for (int i = 0; i < 10; i++) {
                sum2 += Character.getNumericValue(digits.charAt(i)) * (11 - i);
            }
            int check2 = 11 - (sum2 % 11);
            if (check2 >= 10)
                check2 = 0;
            return check2 == Character.getNumericValue(digits.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }
}