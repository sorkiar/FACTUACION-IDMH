ALTER TABLE client
    MODIFY COLUMN phone_1 VARCHAR(25) NULL,
    MODIFY COLUMN phone_2 VARCHAR(25) NULL,
    ADD COLUMN country_code_1 VARCHAR(10) NULL AFTER contact_person_name,
    ADD COLUMN country_code_2 VARCHAR(10) NULL AFTER phone_1;

UPDATE client SET country_code_1 = '51' WHERE phone_1 IS NOT NULL;
UPDATE client SET country_code_2 = '51' WHERE phone_2 IS NOT NULL;
