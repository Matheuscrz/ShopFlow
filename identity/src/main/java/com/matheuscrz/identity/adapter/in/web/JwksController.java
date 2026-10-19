package com.matheuscrz.identity.adapter.in.web;

import com.matheuscrz.identity.config.RsaKeyProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "JWKS", description = "Chaves públicas para validação de tokens pelos Resource Servers")
public class JwksController {

    private final RsaKeyProvider rsaKeyProvider;

    public JwksController(RsaKeyProvider rsaKeyProvider) {
        this.rsaKeyProvider = rsaKeyProvider;
    }

    @GetMapping(value = "/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obter JWKS", description = "Retorna as chaves públicas ativas para validação de tokens.")
    public Map<String, Object> getJwks() {
        return rsaKeyProvider.getPublicJwkSet().toJSONObject();
    }
}
