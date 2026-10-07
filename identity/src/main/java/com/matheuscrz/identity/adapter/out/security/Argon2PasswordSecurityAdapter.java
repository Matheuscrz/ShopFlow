package com.matheuscrz.identity.adapter.out.security;

import com.matheuscrz.identity.application.port.out.PasswordSecurityPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Argon2PasswordSecurityAdapter implements PasswordSecurityPort {

    private final PasswordEncoder passwordEncoder;

    public Argon2PasswordSecurityAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}