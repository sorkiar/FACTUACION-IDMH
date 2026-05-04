-- Assign Configuracion (NAVBAR) to the Administrador profile (was missing from V033 seed)
INSERT IGNORE INTO profile_menu (profile_id, menu_id)
SELECT p.id, 6
FROM profile p
WHERE p.name = 'Administrador';
