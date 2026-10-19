-- V9__add_phone_lookup_index.sql
CREATE UNIQUE INDEX IF NOT EXISTS uk_users_phone_lookup_hash
    ON users (phone_lookup_hash)
    WHERE phone_lookup_hash IS NOT NULL;

COMMENT ON COLUMN users.phone_ciphertext IS
    'Telefone criptografado com AES-256-GCM; nunca armazenar em texto puro';

COMMENT ON COLUMN users.phone_lookup_hash IS
    'HMAC-SHA-256 do telefone normalizado para busca e unicidade';