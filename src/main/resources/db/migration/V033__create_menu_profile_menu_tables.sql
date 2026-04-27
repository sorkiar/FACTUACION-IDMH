-- ============================================================
-- V033: Menu & Profile permissions
--   1. Make profile.code nullable (to allow insert without code)
--   2. Create menu table
--   3. Create profile_menu join table
--   4. Seed menus from frontend route/sidebar structure
--   5. Insert default 'Administrador' profile
--   6. Assign all menus to Administrador
--   7. Update existing users to use Administrador profile
-- ============================================================

-- 1. Make profile.code nullable
ALTER TABLE profile
    MODIFY COLUMN code VARCHAR(20) NULL;

-- 2. Menu table
CREATE TABLE menu (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    path       VARCHAR(200) NULL,
    parent_id  BIGINT       NULL,
    sort_order INT          NOT NULL DEFAULT 1,
    CONSTRAINT fk_menu_parent FOREIGN KEY (parent_id) REFERENCES menu (id)
);

-- 3. Profile-menu join table
CREATE TABLE profile_menu (
    profile_id BIGINT NOT NULL,
    menu_id    BIGINT NOT NULL,
    PRIMARY KEY (profile_id, menu_id),
    CONSTRAINT fk_pm_profile FOREIGN KEY (profile_id) REFERENCES profile (id),
    CONSTRAINT fk_pm_menu    FOREIGN KEY (menu_id)    REFERENCES menu (id)
);

-- 4. Seed menus

-- Top-level single items
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Inicio', '/', NULL, 1);
SET @m_inicio = LAST_INSERT_ID();

-- Parent groups (no path)
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Inventario', NULL, NULL, 2);
SET @m_inventario = LAST_INSERT_ID();

INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('General', NULL, NULL, 3);
SET @m_general = LAST_INSERT_ID();

INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Facturación', NULL, NULL, 4);
SET @m_facturacion = LAST_INSERT_ID();

INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Reportes', NULL, NULL, 5);
SET @m_reportes = LAST_INSERT_ID();

INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Configuración', '/configuration', NULL, 6);
SET @m_config = LAST_INSERT_ID();

INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Seguridad', NULL, NULL, 7);
SET @m_seguridad = LAST_INSERT_ID();

-- Inventario children
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Productos',  '/products',  @m_inventario, 1);
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Servicios',  '/services',  @m_inventario, 2);

-- General children
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Clientes',       '/clients',  @m_general, 1);
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Transportistas', '/carriers', @m_general, 2);
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Conductores',    '/drivers',  @m_general, 3);

-- Facturación children
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Ventas',                    '/sales',                @m_facturacion, 1);
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Notas de Crédito / Débito', '/credit-debit-notes',   @m_facturacion, 2);
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Guías de Remisión',          '/remission-guides',     @m_facturacion, 3);
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Adm. Comprobantes',         '/sunat-documents',      @m_facturacion, 4);

-- Reportes children
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Reporte de Ventas', '/reports/sales', @m_reportes, 1);

-- Seguridad children
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Perfiles', '/profiles', @m_seguridad, 1);
INSERT INTO menu (name, path, parent_id, sort_order) VALUES ('Usuarios', '/users',    @m_seguridad, 2);

-- 5. Insert default Administrador profile
INSERT INTO profile (name, status, created_by) VALUES ('Administrador', 1, 'migration');
SET @admin_profile_id = LAST_INSERT_ID();

-- 6. Assign all menus to Administrador
INSERT INTO profile_menu (profile_id, menu_id)
SELECT @admin_profile_id, id FROM menu;

-- 7. Assign Administrador to all existing users
UPDATE `user` SET profile_id = @admin_profile_id;
