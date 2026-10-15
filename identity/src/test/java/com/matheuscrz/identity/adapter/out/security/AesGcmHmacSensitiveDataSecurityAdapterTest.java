package com.matheuscrz.identity.adapter.out.security;

import com.matheuscrz.identity.application.port.out.SensitiveDataSecurityPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AesGcmHmacSensitiveDataSecurityAdapterTest {

    private SensitiveDataSecurityPort adapter;

    @BeforeEach
    void setUp() {
        adapter = new AesGcmHmacSensitiveDataSecurityAdapter(
                "chave-para-cifrar-aes-256-secreta-muito-segura!",
                "chave-para-lookup-hmac-sha256-secreta!");
    }

    @Test
    @DisplayName("Deve criptografar e decriptografar qualquer dado sensível com sucesso")
    void shouldEncryptAndDecryptAnySensitiveData() {
        String original = "usuario@shopflow.com";
        String ciphertext = adapter.encrypt(original);

        assertThat(ciphertext)
                .isNotBlank()
                .isNotEqualTo(original);
        assertThat(adapter.decrypt(ciphertext))
                .isEqualTo(original);
    }

    @Test
    @DisplayName("Ciphertext deve ser não determinístico (IV aleatório por operação)")
    void ciphertextMustBeNonDeterministic() {
        String original = "52998224725";
        assertThat(adapter.encrypt(original))
                .isNotEqualTo(adapter.encrypt(original));
    }

    @Test
    @DisplayName("Lookup hash deve ser determinístico, normalizado em minúsculas e ter 64 caracteres hex")
    void lookupHashMustBeDeterministicAndNormalized() {
        String hash1 = adapter.generateLookupHash("USUARIO@SHOPFLOW.COM ");
        String hash2 = adapter.generateLookupHash("usuario@shopflow.com");

        assertThat(hash1)
                .hasSize(64)
                .isEqualTo(hash2);
    }

    @Test
    @DisplayName("Deve rejeitar ciphertext adulterado com SecurityException")
    void shouldRejectTamperedCiphertext() {
        String ciphertext = adapter.encrypt("dado sensível");
        String tampered = ciphertext.substring(0, ciphertext.length() - 2) + "AA";

        assertThatThrownBy(() -> adapter.decrypt(tampered))
                .isInstanceOf(SecurityException.class);
    }
}