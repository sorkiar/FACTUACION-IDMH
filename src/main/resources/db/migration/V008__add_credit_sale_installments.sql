-- Agregar tipo de pago (CONTADO / CREDITO) a la tabla de venta
ALTER TABLE sale
    ADD COLUMN payment_type VARCHAR(10) NOT NULL DEFAULT 'CONTADO';

-- Tabla de cuotas para ventas a crédito
CREATE TABLE sale_installment
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_id            BIGINT UNSIGNED NOT NULL,
    installment_number INTEGER         NOT NULL,
    due_date           DATE            NOT NULL,
    amount             DECIMAL(14, 2)  NOT NULL,

    -- Auditoría
    created_at         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by         VARCHAR(50)     NOT NULL,
    updated_at         DATETIME,
    updated_by         VARCHAR(50),
    deleted_at         DATETIME,
    deleted_by         VARCHAR(50),

    CONSTRAINT fk_sale_installment_sale FOREIGN KEY (sale_id) REFERENCES sale (id)
);
