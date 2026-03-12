-- V007: Configuración de modo de envío a SUNAT (ONLINE/OFFLINE) por tipo de comprobante
-- Grupo: sunat_envio
-- Claves por tipo: <tipo>_modo (ONLINE|OFFLINE) y <tipo>_intervalo_minutos (para modo OFFLINE)

INSERT INTO configuration (config_group, config_key, config_value, config_datatype, description, created_by)
VALUES
  ('sunat_envio', 'boleta_modo',                   'ONLINE',  'STRING',  'Modo de envío de boletas a SUNAT: ONLINE (inmediato al crear) u OFFLINE (programado)',       'system'),
  ('sunat_envio', 'boleta_intervalo_minutos',       '30',      'INTEGER', 'Intervalo en minutos para envío offline de boletas (aplica solo en modo OFFLINE)',           'system'),
  ('sunat_envio', 'factura_modo',                  'ONLINE',  'STRING',  'Modo de envío de facturas a SUNAT: ONLINE (inmediato al crear) u OFFLINE (programado)',      'system'),
  ('sunat_envio', 'factura_intervalo_minutos',      '30',      'INTEGER', 'Intervalo en minutos para envío offline de facturas (aplica solo en modo OFFLINE)',          'system'),
  ('sunat_envio', 'nota_credito_modo',              'ONLINE',  'STRING',  'Modo de envío de notas de crédito a SUNAT: ONLINE (inmediato) u OFFLINE (programado)',      'system'),
  ('sunat_envio', 'nota_credito_intervalo_minutos', '30',      'INTEGER', 'Intervalo en minutos para envío offline de notas de crédito',                               'system'),
  ('sunat_envio', 'nota_debito_modo',               'ONLINE',  'STRING',  'Modo de envío de notas de débito a SUNAT: ONLINE (inmediato) u OFFLINE (programado)',       'system'),
  ('sunat_envio', 'nota_debito_intervalo_minutos',  '30',      'INTEGER', 'Intervalo en minutos para envío offline de notas de débito',                                'system'),
  ('sunat_envio', 'guia_remision_modo',             'ONLINE',  'STRING',  'Modo de envío de guías de remisión a SUNAT: ONLINE (inmediato) u OFFLINE (programado)',     'system'),
  ('sunat_envio', 'guia_remision_intervalo_minutos','30',      'INTEGER', 'Intervalo en minutos para envío offline de guías de remisión',                              'system');
