-- Orden de compra en la venta
ALTER TABLE sale
    ADD COLUMN purchase_order VARCHAR(50) NULL;

-- Guías relacionadas (múltiples por venta)
CREATE TABLE sale_related_guide (
    id           BIGINT      AUTO_INCREMENT PRIMARY KEY,
    sale_id      BIGINT      NOT NULL,
    guide_number VARCHAR(50) NOT NULL,
    CONSTRAINT fk_sale_related_guide_sale FOREIGN KEY (sale_id) REFERENCES sale(id)
);
