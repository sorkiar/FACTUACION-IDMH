-- V029: Agregar dirección del cliente directamente en sale y remission_guide
-- Para sale: client_address_id (FK referencial, nullable) + client_address (texto del comprobante, nullable para registros existentes)
-- Para remission_guide: client_address (texto del comprobante, nullable para registros existentes)

-- sale
ALTER TABLE sale
    ADD COLUMN client_address_id BIGINT        NULL,
    ADD COLUMN client_address    VARCHAR(500)  NULL,
    ADD CONSTRAINT fk_sale_client_address FOREIGN KEY (client_address_id) REFERENCES client_address(id);

-- remission_guide (client_address_id ya existe como FK)
ALTER TABLE remission_guide
    ADD COLUMN client_address VARCHAR(500) NULL;

-- Poblar guías existentes tomando la dirección del destinatario como dirección del cliente
UPDATE remission_guide
SET client_address = destination_address
WHERE deleted_at IS NULL
  AND destination_address IS NOT NULL;
