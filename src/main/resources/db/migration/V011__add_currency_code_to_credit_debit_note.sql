ALTER TABLE credit_debit_note
    ADD COLUMN currency_code VARCHAR(4) NOT NULL DEFAULT 'PEN'
    AFTER tax_percentage;
