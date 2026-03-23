ALTER TABLE sale
  ADD COLUMN detraction_code   VARCHAR(3)    NULL COMMENT 'SUNAT catalog 54 code' AFTER has_detraction,
  ADD COLUMN detraction_rate   DECIMAL(5,2)  NULL AFTER detraction_code,
  ADD COLUMN detraction_amount DECIMAL(14,2) NULL COMMENT 'Always in PEN' AFTER detraction_rate;
