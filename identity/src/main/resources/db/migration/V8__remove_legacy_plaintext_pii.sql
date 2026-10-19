-- Execute somente após garantir que os dados existentes foram migrados.
ALTER TABLE users
    DROP COLUMN IF EXISTS email;

ALTER TABLE user_addresses
    DROP COLUMN IF EXISTS zip_code,
    DROP COLUMN IF EXISTS street,
    DROP COLUMN IF EXISTS number,
    DROP COLUMN IF EXISTS complement,
    DROP COLUMN IF EXISTS neighborhood,
    DROP COLUMN IF EXISTS city,
    DROP COLUMN IF EXISTS state,
    DROP COLUMN IF EXISTS country;

ALTER TABLE users
    ALTER COLUMN email_ciphertext SET NOT NULL,
    ALTER COLUMN email_lookup_hash SET NOT NULL;

ALTER TABLE user_addresses
    ALTER COLUMN zip_code_ciphertext SET NOT NULL,
    ALTER COLUMN street_ciphertext SET NOT NULL,
    ALTER COLUMN number_ciphertext SET NOT NULL,
    ALTER COLUMN neighborhood_ciphertext SET NOT NULL,
    ALTER COLUMN city_ciphertext SET NOT NULL,
    ALTER COLUMN state_ciphertext SET NOT NULL,
    ALTER COLUMN country_ciphertext SET NOT NULL;