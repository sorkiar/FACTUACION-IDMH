-- V022: Agrega columna editable a la tabla configuration
-- 0 = no editable (uso interno del sistema), 1 = editable por el usuario
ALTER TABLE configuration
    ADD COLUMN editable INT NOT NULL DEFAULT 0
        COMMENT '0=no editable (uso interno), 1=editable por el usuario'
        AFTER description;
