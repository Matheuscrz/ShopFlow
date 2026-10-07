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
        PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        this.adapter = new Argon2PasswordSecurityAdapter(encoder);
    }

    @Test
    @DisplayName("Hash gerado deve começar estritamente com $argon2id$")
    void passwordHashMustBeArgon2id() {
        String raw = "MinhaSenhaForte#2026";
        String encoded = adapter.encode(raw);

        assertThat(encoded).startsWith("$argon2id$");
        assertThat(adapter.matches(raw, encoded)).isTrue();
    }

    @Test
    @DisplayName("Senhas incorretas devem falhar na verificação")
    void wrongPasswordShouldNotMatch() {
        String encoded = adapter.encode("SenhaCorreta123");
        assertThat(adapter.matches("SenhaIncorreta123", encoded)).isFalse();
    }
}