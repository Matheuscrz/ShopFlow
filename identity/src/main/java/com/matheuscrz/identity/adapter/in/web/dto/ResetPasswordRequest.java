package com.matheuscrz.identity.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "O token é obrigatório")
        String token,
        
        @NotBlank(message = "A nova senha é obrigatória")
        @Size(min = 8, max = 200, message = "A senha deve ter entre 8 e 200 caracteres")
        String newPassword
) {}