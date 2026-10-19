package com.matheuscrz.identity.adapter.out.security;

import com.matheuscrz.identity.application.port.out.SensitiveDataSecurityPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

@Component
public class AesGcmHmacSensitiveDataSecurityAdapter implements SensitiveDataSecurityPort {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private final SecretKey encryptionKey;
    private final SecretKey lookupKey;
    private final SecureRandom secureRandom;

    public AesGcmHmacSensitiveDataSecurityAdapter(
            @Value("${app.security.data-protection.encryption-key}") String rawEncryptionKey,
            @Value("${app.security.data-protection.lookup-key}") String rawLookupKey) {

        this.encryptionKey = deriveAesKey(rawEncryptionKey);
        this.lookupKey = new SecretKeySpec(
                rawLookupKey.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM
        );
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isBlank()) {
            return null;
        }

        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Concatena IV + Ciphertext
            byte[] combined = ByteBuffer.allocate(iv.length + cipherBytes.length)
                    .put(iv)
                    .put(cipherBytes)
                    .array();

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new SecurityException("Erro ao criptografar dado sensível", e);
        }
    }

    @Override
    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isBlank()) {
            return null;
        }

        try {
            byte[] combined = Base64.getDecoder().decode(cipherText);

            if (combined.length < GCM_IV_LENGTH_BYTES) {
                throw new IllegalArgumentException("Payload cifrado inválido: tamanho insuficiente.");
            }

            ByteBuffer buffer = ByteBuffer.wrap(combined);
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            buffer.get(iv);

            byte[] cipherBytes = new byte[buffer.remaining()];
            buffer.get(cipherBytes);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] plainBytes = cipher.doFinal(cipherBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SecurityException("Erro ao decriptografar dado sensível", e);
        }
    }

    @Override
    public String generateLookupHash(String plainText) {
        if (plainText == null || plainText.isBlank()) {
            return null;
        }

        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(lookupKey);
            byte[] hmacBytes = mac.doFinal(plainText.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacBytes);
        } catch (Exception e) {
            throw new SecurityException("Erro ao gerar lookup hash determinístico", e);
        }
    }

    private SecretKey deriveAesKey(String rawKey) {
        try {
            byte[] keyBytes = rawKey.getBytes(StandardCharsets.UTF_8);
            // Garante 256 bits (32 bytes) usando SHA-256 caso a chave informada seja de qualquer tamanho
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] derivedKey = sha256.digest(keyBytes);
            return new SecretKeySpec(derivedKey, AES_ALGORITHM);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao derivar chave AES-256", e);
        }
    }
}