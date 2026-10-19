ALTER TABLE users
    ADD COLUMN IF NOT EXISTS email_ciphertext TEXT,
    ADD COLUMN IF NOT EXISTS email_lookup_hash VARCHAR(64),
    ADD COLUMN IF NOT EXISTS phone_ciphertext TEXT,
    ADD COLUMN IF NOT EXISTS phone_lookup_hash VARCHAR(64);

CREATE UNIQUE INDEX IF NOT EXISTS uk_users_email_lookup_hash
    ON users (email_lookup_hash)
    WHERE email_lookup_hash IS NOT NULL;

ALTER TABLE user_addresses
    ADD COLUMN IF NOT EXISTS zip_code_ciphertext TEXT,
    ADD COLUMN IF NOT EXISTS street_ciphertext TEXT,
    ADD COLUMN IF NOT EXISTS number_ciphertext TEXT,
    ADD COLUMN IF NOT EXISTS complement_ciphertext TEXT,
    ADD COLUMN IF NOT EXISTS neighborhood_ciphertext TEXT,
    ADD COLUMN IF NOT EXISTS city_ciphertext TEXT,
    ADD COLUMN IF NOT EXISTS state_ciphertext TEXT,
    ADD COLUMN IF NOT EXISTS country_ciphertext TEXT;