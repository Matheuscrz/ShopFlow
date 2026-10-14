package com.matheuscrz.identity.adapter.out.security;

import com.matheuscrz.identity.domain.model.Cpf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AesGcmHmacCpfSecurityAdapterTest {

    private AesGcmHmacCpfSecurityAdapter adapter;

    @BeforeEach
    void setUp() {
        this.adapter = new AesGcmHmacCpfSecurityAdapter(
                "chave-para-cifrar-aes-256-secreta-muito-segura!",
                "chave-para-lookup-hmac-sha256-secreta!");
    }

    @Test
    @DisplayName("Deve cifrar e decifrar CPF recuperando o valor original")
    void shouldEncryptAndDecryptCpf() {
        Cpf originalCpf = new Cpf("52998224725");

        String ciphertext = adapter.encrypt(originalCpf);
        assertThat(ciphertext).isNotBlank();
        assertThat(ciphertext).isNotEqualTo(originalCpf.value());

        Cpf decryptedCpf = adapter.decrypt(ciphertext);
        assertThat(decryptedCpf).isEqualTo(originalCpf);
        assertThat(decryptedCpf.value()).isEqualTo("52998224725");
    }

    @Test
    @DisplayName("Ciphertext deve ser não-determinístico (IV aleatório por execução)")
    void ciphertextsMustBeNonDeterministic() {
        Cpf cpf = new Cpf("52998224725");

        String cipher1 = adapter.encrypt(cpf);
        String cipher2 = adapter.encrypt(cpf);

        assertThat(cipher1).isNotEqualTo(cipher2);
    }

    @Test
    @DisplayName("LookupHash deve ser determinístico e ter 64 caracteres hexadecimais")
    void lookupHashMustBeDeterministic() {
        Cpf cpf1 = new Cpf("52998224725");
        Cpf cpf2 = new Cpf("529.982.247-25");

        String hash1 = adapter.lookupHash(cpf1);
        String hash2 = adapter.lookupHash(cpf2);

        assertThat(hash1).hasSize(64);
        assertThat(hash1).isEqualTo(hash2);
    }
}