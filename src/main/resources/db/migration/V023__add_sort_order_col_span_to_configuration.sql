-- V023: Agrega sort_order y col_span a la tabla configuration
--   sort_order : ordena los registros editables en el formulario del front (0 = primero)
--   col_span   : ancho del campo en el formulario (1 = mitad del row, 2 = row completo)
ALTER TABLE configuration
    ADD COLUMN sort_order INT NOT NULL DEFAULT 0
        COMMENT 'Orden de presentación en el formulario (menor número = primero)'
        AFTER editable,
    ADD COLUMN col_span INT NOT NULL DEFAULT 2
        COMMENT '1=mitad del row, 2=row completo'
        AFTER sort_order;
