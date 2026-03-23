-- Add optional detraction code reference to product table
ALTER TABLE product
    ADD COLUMN detraction_code_id BIGINT NULL,
    ADD CONSTRAINT fk_product_detraction_code
        FOREIGN KEY (detraction_code_id) REFERENCES detraction_code(id);

-- Add optional detraction code reference to service table
ALTER TABLE service
    ADD COLUMN detraction_code_id BIGINT NULL,
    ADD CONSTRAINT fk_service_detraction_code
        FOREIGN KEY (detraction_code_id) REFERENCES detraction_code(id);
