-- Agrega columnas xml_url y cdr_url a la tabla document
ALTER TABLE document ADD COLUMN xml_url VARCHAR(500);
ALTER TABLE document ADD COLUMN cdr_url VARCHAR(500);

-- Agrega columnas xml_url y cdr_url a la tabla credit_debit_note
ALTER TABLE credit_debit_note ADD COLUMN xml_url VARCHAR(500);
ALTER TABLE credit_debit_note ADD COLUMN cdr_url VARCHAR(500);
