package com.matheuscrz.identity.application.port.out;

public interface SensitiveDataSecurityPort {

    /**
     * Criptografa o dado em texto plano utilizando cifra autenticada (AES-256-GCM).
     * Cada chamada gera um IV aleatório diferente.
     */
    String encrypt(String plainText);

    /**
     * Decriptografa o texto cifrado (IV + Ciphertext + Auth Tag).
     */
    String decrypt(String cipherText);

    /**
     * Gera um hash cego determinístico (HMAC-SHA256) para possibilitar buscas 
     * exatas e índices únicos no banco de dados (ex: busca por CPF ou E-mail).
     */
    String generateLookupHash(String plainText);
}