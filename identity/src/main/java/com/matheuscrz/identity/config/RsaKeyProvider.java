package com.matheuscrz.identity.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyProvider {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final String kid;
    private final JWKSet jwkSet;

    public RsaKeyProvider(JwtProperties properties) {
        this.kid = requireConfigured(properties.getKid(), "kid");

        if (hasText(properties.getPrivateKey()) && hasText(properties.getPublicKey())) {
            this.privateKey = parsePrivateKey(properties.getPrivateKey());
            this.publicKey = parsePublicKey(properties.getPublicKey());
        } else if (!hasText(properties.getPrivateKey()) && !hasText(properties.getPublicKey())) {
            KeyPair keyPair = generateRsaKeyPair();
            this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
            this.publicKey = (RSAPublicKey) keyPair.getPublic();
        } else {
            throw new IllegalArgumentException(
                    "As chaves privada e pública RSA devem ser configuradas em conjunto");
        }

        this.jwkSet = new JWKSet(new RSAKey.Builder(this.publicKey)
                .keyID(this.kid)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(com.nimbusds.jose.JWSAlgorithm.RS256)
                .build());
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public String getKid() {
        return kid;
    }

    public JWKSet getPublicJwkSet() {
        return jwkSet.toPublicJWKSet();
    }

    private static KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Algoritmo RSA indisponível", ex);
        }
    }

    private static RSAPrivateKey parsePrivateKey(String keyPem) {
        try {
            byte[] bytes = Base64.getDecoder().decode(cleanPem(keyPem));
            return (RSAPrivateKey) KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(bytes));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Chave privada RSA inválida", ex);
        }
    }

    private static RSAPublicKey parsePublicKey(String keyPem) {
        try {
            byte[] bytes = Base64.getDecoder().decode(cleanPem(keyPem));
            return (RSAPublicKey) KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(bytes));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Chave pública RSA inválida", ex);
        }
    }

    private static String cleanPem(String pem) {
        return pem.replaceAll("-----BEGIN [A-Z ]+-----", "")
                .replaceAll("-----END [A-Z ]+-----", "")
                .replaceAll("\\s+", "");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String requireConfigured(String value, String name) {
        if (!hasText(value)) {
            throw new IllegalArgumentException("Configuração JWT obrigatória ausente: " + name);
        }
        return value;
    }
}
