ALTER TABLE users
    ADD COLUMN cpf_ciphertext TEXT,
    ADD COLUMN cpf_lookup_hash CHAR(64);

CREATE UNIQUE INDEX uk_users_cpf_lookup_hash
    ON users (cpf_lookup_hash)
    WHERE cpf_lookup_hash IS NOT NULL;

COMMENT ON COLUMN users.cpf_ciphertext IS
    'CPF criptografado com AES-GCM; nunca armazenar CPF em texto puro';

COMMENT ON COLUMN users.cpf_lookup_hash IS
    'HMAC-SHA-256 do CPF normalizado para busca e unicidade';