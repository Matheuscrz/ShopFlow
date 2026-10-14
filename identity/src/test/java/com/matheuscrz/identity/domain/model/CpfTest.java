package com.matheuscrz.identity.domain.model;

import com.matheuscrz.identity.domain.exception.InvalidUserDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CpfTest {

    @Test
    @DisplayName("Deve instanciar CPF válido com ou sem pontuação e sanitizar apenas para dígitos")
    void shouldSanitizeAndCreateValidCpf() {
        Cpf cpfComPontos = new Cpf("529.982.247-25");
        Cpf cpfApenasDigitos = new Cpf("52998224725");

        assertThat(cpfComPontos.value()).isEqualTo("52998224725");
        assertThat(cpfApenasDigitos.value()).isEqualTo("52998224725");
        assertThat(cpfApenasDigitos.formatted()).isEqualTo("529.982.247-25");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000000",
            "11111111111",
            "22222222222",
            "33333333333",
            "44444444444",
            "55555555555",
            "66666666666",
            "77777777777",
            "88888888888",
            "99999999999"
    })
    @DisplayName("Deve rejeitar CPFs com todos os dígitos repetidos")
    void shouldRejectRepeatedDigits(String invalidCpf) {
        assertThatThrownBy(() -> new Cpf(invalidCpf))
                .isInstanceOf(InvalidUserDataException.class)
                .hasMessageContaining("CPF inválido");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "123",
            "1234567890",
            "123456789012",
            "abc12345678",
            "52998224726"
    })
    @DisplayName("Deve falhar para formatos incorretos ou dígitos verificadores inválidos")
    void shouldRejectInvalidCpf(String invalidCpf) {
        assertThatThrownBy(() -> new Cpf(invalidCpf))
                .isInstanceOf(InvalidUserDataException.class)
                .hasMessageContaining("CPF inválido");
    }
}