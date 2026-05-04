-- Add configurable logo URL for the company (empresa_emisora group)
INSERT IGNORE INTO configuration (config_group, config_key, config_value, config_datatype, description, editable, created_by)
VALUES ('empresa_emisora', 'emprLogo', '', 'STRING', 'URL del logo de la empresa (usado en PDFs). Si está vacío se usa el logo por defecto del servidor.', 1, 'system');
