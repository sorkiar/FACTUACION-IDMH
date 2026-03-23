ALTER TABLE sale_item
  ADD COLUMN detraction_code_id BIGINT NULL AFTER service_id,
  ADD CONSTRAINT fk_sale_item_detraction_code
    FOREIGN KEY (detraction_code_id) REFERENCES detraction_code(id);
