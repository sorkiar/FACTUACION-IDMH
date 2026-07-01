-- V036 set all menus to SIDEBAR; Configuración is a direct-link top-level item → NAVBAR
UPDATE menu SET menu_type = 'NAVBAR' WHERE name = 'Configuración' AND parent_id IS NULL;
