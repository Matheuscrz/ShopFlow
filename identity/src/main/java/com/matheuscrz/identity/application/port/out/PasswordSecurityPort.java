package com.matheuscrz.identity.application.port.out;

public interface PasswordSecurityPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
