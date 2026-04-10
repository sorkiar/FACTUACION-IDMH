-- =============================================================
-- V026: Eliminar tabla recipient, agregar client_address,
--       migrar destinatarios a clientes y actualizar guías.
-- =============================================================

-- 1. Hacer nullable la columna address de client (pasa a client_address)
ALTER TABLE client MODIFY COLUMN address VARCHAR(500) NULL;

-- 2. Crear tabla client_address
CREATE TABLE client_address (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    client_id   BIGINT       NOT NULL,
    address     VARCHAR(500) NOT NULL,
    ubigeo      VARCHAR(10)  NULL,
    description VARCHAR(255) NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  VARCHAR(50)  NOT NULL DEFAULT 'system',
    updated_at  DATETIME     NULL,
    updated_by  VARCHAR(50)  NULL,
    deleted_at  DATETIME     NULL,
    deleted_by  VARCHAR(50)  NULL,
    CONSTRAINT fk_client_address_client FOREIGN KEY (client_id) REFERENCES client(id)
);

-- 3. Migrar direcciones existentes de client → client_address
INSERT INTO client_address (client_id, address, ubigeo, created_by)
SELECT id, address, NULL, COALESCE(created_by, 'system')
FROM client
WHERE address IS NOT NULL AND TRIM(address) != '' AND deleted_at IS NULL;

-- 4. Migrar recipients con doc_type = 'DNI' → cliente persona natural (document_type_id=1)
--    Solo inserta si no existe ya un cliente con el mismo documento
INSERT INTO client (person_type_id, document_type_id, document_number, first_name, last_name,
                   retention_agent, status, created_by)
SELECT dt.person_type_id, dt.id, r.doc_number, r.name, NULL,
       0, 1, COALESCE(r.created_by, 'system')
FROM recipient r
INNER JOIN document_type dt ON dt.id = 1
WHERE r.doc_type = 'DNI'
  AND r.deleted_at IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM client c
      WHERE c.document_type_id = 1
        AND c.document_number = r.doc_number
        AND c.deleted_at IS NULL
  );

-- 5. Migrar recipients con doc_type = 'RUC' → cliente persona jurídica (document_type_id=2)
INSERT INTO client (person_type_id, document_type_id, document_number, business_name,
                   retention_agent, status, created_by)
SELECT dt.person_type_id, dt.id, r.doc_number, r.name,
       0, 1, COALESCE(r.created_by, 'system')
FROM recipient r
INNER JOIN document_type dt ON dt.id = 2
WHERE r.doc_type = 'RUC'
  AND r.deleted_at IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM client c
      WHERE c.document_type_id = 2
        AND c.document_number = r.doc_number
        AND c.deleted_at IS NULL
  );

-- 6. Migrar direcciones de recipient → client_address (para los que tienen dirección)
INSERT INTO client_address (client_id, address, ubigeo, created_by)
SELECT c.id, r.address, NULL, COALESCE(r.created_by, 'system')
FROM recipient r
INNER JOIN client c ON (
    (r.doc_type = 'DNI' AND c.document_type_id = 1 AND c.document_number = r.doc_number)
    OR
    (r.doc_type = 'RUC' AND c.document_type_id = 2 AND c.document_number = r.doc_number)
)
WHERE r.deleted_at IS NULL
  AND r.address IS NOT NULL
  AND TRIM(r.address) != ''
  AND c.deleted_at IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM client_address ca
      WHERE ca.client_id = c.id AND ca.address = r.address AND ca.deleted_at IS NULL
  );

-- 7. Agregar columnas client_id y client_address_id a remission_guide
ALTER TABLE remission_guide
    ADD COLUMN client_id         BIGINT NULL,
    ADD COLUMN client_address_id BIGINT NULL;

-- 8. Poblar client_id en remission_guide desde recipient → client
UPDATE remission_guide rg
    INNER JOIN recipient r ON r.id = rg.recipient_id
    INNER JOIN client c ON (
        (r.doc_type = 'DNI' AND c.document_type_id = 1 AND c.document_number = r.doc_number)
        OR
        (r.doc_type = 'RUC' AND c.document_type_id = 2 AND c.document_number = r.doc_number)
    )
SET rg.client_id = c.id
WHERE c.deleted_at IS NULL;

-- 9. Agregar FK de remission_guide.client_id → client
ALTER TABLE remission_guide
    ADD CONSTRAINT fk_rg_client FOREIGN KEY (client_id) REFERENCES client(id);

-- 10. Agregar FK de remission_guide.client_address_id → client_address
ALTER TABLE remission_guide
    ADD CONSTRAINT fk_rg_client_address FOREIGN KEY (client_address_id) REFERENCES client_address(id);

-- 11. Eliminar FK y columna recipient_id de remission_guide
ALTER TABLE remission_guide DROP FOREIGN KEY fk_rg_recipient;
ALTER TABLE remission_guide DROP COLUMN recipient_id;

-- 12. Eliminar tabla recipient
DROP TABLE IF EXISTS recipient;
