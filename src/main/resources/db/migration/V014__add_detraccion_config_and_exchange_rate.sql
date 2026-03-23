-- V014: Parámetros de detracción/retención y tabla de tipo de cambio USD

-- ============================================================
-- 1. Configuración: detracción y retención
-- ============================================================
INSERT INTO configuration (config_group, config_key, config_value, config_datatype, description, created_by)
VALUES
  ('detraccion_retencion', 'min_detraccion_amount',   '700',            'DECIMAL', 'Monto mínimo de operación para aplicar detracción (S/)',     'system'),
  ('detraccion_retencion', 'min_retencion_amount',    '700',            'DECIMAL', 'Monto mínimo de operación para aplicar retención (S/)',      'system'),
  ('detraccion_retencion', 'retencion_rate',          '3',              'DECIMAL', 'Tasa de retención (%)',                                      'system'),
  ('detraccion_retencion', 'banco_nacion_detraccion', '00074235867',    'STRING',  'Cuenta Banco de la Nación de la empresa para detracciones',  'system');

-- ============================================================
-- 2. Configuración: tipo de cambio
-- ============================================================
INSERT INTO configuration (config_group, config_key, config_value, config_datatype, description, created_by)
VALUES
  ('tipo_cambio', 'fetch_hour',   '9',                                           'INTEGER', 'Hora de obtención del tipo de cambio SUNAT (0-23)',         'system'),
  ('tipo_cambio', 'sunat_token',  'zdqjevo3ycsmfef9fqts53od7wsjl6fbw2db99frrtku7by9qkln', 'STRING', 'Token para consulta de tipo de cambio en SUNAT', 'system');

-- ============================================================
-- 3. Tabla: exchange_rate
-- ============================================================
CREATE TABLE exchange_rate (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    rate_date  DATE         NOT NULL COMMENT 'Fecha de publicación',
    value      DECIMAL(10,3) NOT NULL COMMENT 'Valor del tipo de cambio',
    type       VARCHAR(1)   NOT NULL COMMENT 'C=Compra, V=Venta',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_exchange_rate_date_type (rate_date, type)
);
