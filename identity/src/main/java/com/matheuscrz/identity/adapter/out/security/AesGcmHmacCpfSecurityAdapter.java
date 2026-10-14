package com.matheuscrz.identity.adapter.out.security;

import com.matheuscrz.identity.application.port.out.CpfSecurityPort;
import com.matheuscrz.identity.domain.model.Cpf;
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
public class AesGcmHmacCpfSecurityAdapter implements CpfSecurityPort {

    private static final String AES_ALGO = "AES/GCM/NoPadding";
    private static final String HMAC_ALGO = "HmacSHA256";
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private final SecretKey encryptionKey;
    private final SecretKey lookupKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public AesGcmHmacCpfSecurityAdapter(
            @Value("${app.security.cpf.encryption-key}") String rawEncryptionKey,
            @Value("${app.security.cpf.lookup-key}") String rawLookupKey) {
        this.encryptionKey = deriveKey(rawEncryptionKey);
        this.lookupKey = new SecretKeySpec(rawLookupKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
    }

    private SecretKey deriveKey(String secret) {
        try {
            byte[] sha256 = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(sha256, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao inicializar chave criptográfica do CPF", e);
        }
    }

    @Override
    public String encrypt(Cpf cpf) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] plaintext = cpf.value().getBytes(StandardCharsets.UTF_8);
            byte[] ciphertext = cipher.doFinal(plaintext);

            byte[] combined = ByteBuffer.allocate(iv.length + ciphertext.length)
                    .put(iv)
                    .put(ciphertext)
                    .array();

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao cifrar CPF", e);
        }
    }

    @Override
    public Cpf decrypt(String encryptedCpf) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedCpf);
            ByteBuffer buffer = ByteBuffer.wrap(combined);

            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            buffer.get(iv);

            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance(AES_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new Cpf(new String(plaintext, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao decifrar CPF", e);
        }
    }

    @Override
    public String lookupHash(Cpf cpf) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(lookupKey);
            byte[] hmac = mac.doFinal(cpf.value().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmac);
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao gerar lookup hash do CPF", e);
        }
    }
}