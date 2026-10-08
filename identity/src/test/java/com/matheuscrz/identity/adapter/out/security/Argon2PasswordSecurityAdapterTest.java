package com.matheuscrz.identity.adapter.out.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class Argon2PasswordSecurityAdapterTest {

    private Argon2PasswordSecurityAdapter adapter;

    @BeforeEach
    void setUp() {
        PasswordEncoder encoder = new Argon2PasswordEncoder(
                32,
                64,
                1,
                65_536,
                3);

        this.adapter = new Argon2PasswordSecurityAdapter(encoder);
    }

    @Test
    @DisplayName("Hash deve usar Argon2id com os parâmetros configurados")
    void passwordHashMustUseConfiguredArgon2idParameters() {
        String raw = "MinhaSenhaForte#2026";
        String encoded = adapter.encode(raw);

        assertThat(encoded)
                .startsWith("$argon2id$")
                .contains("$m=65536,t=3,p=1$");

        assertThat(adapter.matches(raw, encoded)).isTrue();
    }

    @Test
    @DisplayName("Senhas incorretas devem falhar na verificação")
    void wrongPasswordShouldNotMatch() {
        String encoded = adapter.encode("SenhaCorreta123");

        assertThat(adapter.matches("SenhaIncorreta123", encoded))
                .isFalse();
    }

    @Test
    @DisplayName("Senhas corretas devem passar na verificação")
    void correctPasswordShouldMatch() {
        String encoded = adapter.encode("SenhaCorreta123");

        assertThat(adapter.matches("SenhaCorreta123", encoded))
                .isTrue();
    }
}