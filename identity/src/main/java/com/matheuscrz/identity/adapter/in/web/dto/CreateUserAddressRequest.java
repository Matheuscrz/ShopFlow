package com.matheuscrz.identity.adapter.in.web.dto;

import com.matheuscrz.identity.application.service.UserInputNormalizer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserAddressRequest(
        @NotBlank(message = "CEP é obrigatório") @Size(max = 20, message = "O CEP deve ter no máximo 20 caracteres") String zipCode,

        @NotBlank(message = "Rua é obrigatória") @Size(max = 150, message = "A rua deve ter no máximo 150 caracteres") String street,

        @NotBlank(message = "Número é obrigatório") @Size(max = 20, message = "O número deve ter no máximo 20 caracteres") String number,

        @Size(max = 150, message = "O complemento deve ter no máximo 150 caracteres") String complement,

        @NotBlank(message = "Bairro é obrigatório") @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres") String neighborhood,

        @NotBlank(message = "Cidade é obrigatória") @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres") String city,

        @NotBlank(message = "Estado é obrigatório") @Size(max = 100, message = "O estado deve ter no máximo 100 caracteres") String state,

        @NotBlank(message = "País é obrigatório") @Size(max = 100, message = "O país deve ter no máximo 100 caracteres") String country) {

    public CreateUserAddressRequest {
        zipCode = UserInputNormalizer.zipCode(zipCode);
        street = UserInputNormalizer.text(street);
        number = UserInputNormalizer.text(number);
        complement = UserInputNormalizer.text(complement);
        neighborhood = UserInputNormalizer.text(neighborhood);
        city = UserInputNormalizer.text(city);
        state = UserInputNormalizer.text(state);
        country = UserInputNormalizer.text(country);
    }
}