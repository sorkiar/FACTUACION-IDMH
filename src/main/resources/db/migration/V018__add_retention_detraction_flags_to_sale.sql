ALTER TABLE sale
    ADD COLUMN has_retention  TINYINT(1)     NOT NULL DEFAULT 0,
    ADD COLUMN retention_amount DECIMAL(14,2) NULL,
    ADD COLUMN retention_rate   DECIMAL(5,2)  NULL,
    ADD COLUMN has_detraction TINYINT(1)     NOT NULL DEFAULT 0;
