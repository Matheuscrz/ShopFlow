package com.matheuscrz.identity.adapter.in.web.dto;

import com.matheuscrz.identity.application.service.UserInputNormalizer;

import jakarta.validation.constraints.Size;

public record UpdateUserAddressRequest(
        @Size(max = 20, message = "O CEP deve ter no máximo 20 caracteres") String zipCode,

        @Size(max = 150, message = "A rua deve ter no máximo 150 caracteres") String street,

        @Size(max = 20, message = "O número deve ter no máximo 20 caracteres") String number,

        @Size(max = 150, message = "O complemento deve ter no máximo 150 caracteres") String complement,

        @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres") String neighborhood,

        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres") String city,

        @Size(max = 100, message = "O estado deve ter no máximo 100 caracteres") String state,

        @Size(max = 100, message = "O país deve ter no máximo 100 caracteres") String country) {

    public UpdateUserAddressRequest {
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