-- V021: Agrega min_amount al catálogo 54 SUNAT e inserta códigos faltantes
--
-- Cambios no destructivos (sin eliminar registros existentes):
--   1. Agrega columna min_amount: monto mínimo en PEN para que aplique la detracción
--   2. Actualiza min_amount de los códigos Anexo 1 (umbral 1/2 UIT) y deja los demás en S/700
--   3. Corrige la tasa de '021' Movimiento de carga (4% → 10%, RS 071-2018/SUNAT) — era status=0
--   4. Inserta los 3 códigos del catálogo vigente que no existían en V015: 030, 039, 040
--
-- UIT 2026 = S/ 5,500.00  →  1/2 UIT = S/ 2,750.00
-- Códigos Anexo 1 en el catálogo actual (umbral propio de 1/2 UIT):
--   '001' Azúcar y melaza de caña  |  '002' Alcohol etílico  |  '012' Harina/polvo/pellets pescado

-- ─── 1. Agregar columna min_amount ───────────────────────────────────────────
ALTER TABLE detraction_code
    ADD COLUMN min_amount DECIMAL(14,2) NOT NULL DEFAULT 700.00
        COMMENT 'Monto mínimo en PEN a partir del cual aplica la detracción (1/2 UIT o S/700 según SUNAT)'
        AFTER percentage;

-- ─── 2. Actualizar mínimo a 1/2 UIT para los bienes del Anexo 1 ──────────────
-- Estos no usan la regla general de S/700; su umbral propio es 1/2 UIT = S/2,750
UPDATE detraction_code
SET min_amount = 2750.00
WHERE code IN ('001', '002', '012');

-- ─── 3. Corregir tasa de Movimiento de carga (era 4%, vigente 10%) ────────────
UPDATE detraction_code
SET percentage = 10.00
WHERE code = '021';

-- ─── 4. Insertar códigos faltantes del catálogo vigente ──────────────────────
INSERT INTO detraction_code (code, description, percentage, min_amount, category, status) VALUES
('030', 'Contratos de construcción',             4.00, 700.00, 'SERVICIO', 0),
('039', 'Minerales no metálicos',               10.00, 700.00, 'BIEN',     0),
('040', 'Bien inmueble gravado con el IGV',       4.00, 700.00, 'BIEN',     0);
