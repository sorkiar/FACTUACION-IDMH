-- V028: Eliminar columna address de client
-- Las direcciones ya fueron migradas a client_address en V026.
ALTER TABLE client DROP COLUMN address;
