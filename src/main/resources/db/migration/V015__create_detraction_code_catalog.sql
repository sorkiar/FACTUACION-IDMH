-- V015: Catálogo No. 54 - Bienes y servicios sujetos al SPOT (Sistema de Detracciones)
-- Fuente: RS N° 183-2004/SUNAT y modificaciones (RS N° 071-2018, RS N° 082-2018, etc.)
-- NOTA: Verificar porcentajes vigentes contra la resolución de superintendencia actualizada.

CREATE TABLE detraction_code (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    code        VARCHAR(3)    NOT NULL COMMENT 'Código SUNAT Catálogo 54',
    description VARCHAR(500)  NOT NULL COMMENT 'Descripción del bien o servicio',
    percentage  DECIMAL(5,2)  NOT NULL COMMENT 'Porcentaje de detracción aplicable',
    category    VARCHAR(10)   NOT NULL COMMENT 'BIEN o SERVICIO según SUNAT',
    status      TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '1=Activo, 0=Inactivo',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_detraction_code (code)
);

-- ─────────────────────────────────────────────────────────────────────────────
-- BIENES sujetos al SPOT (Anexo 1 de la RS 183-2004/SUNAT)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO detraction_code (code, description, percentage, category, status) VALUES
('001', 'Azúcar y melaza de caña',                                                               10.00, 'BIEN', 0),
('002', 'Alcohol etílico',                                                                        10.00, 'BIEN', 0),
('003', 'Algodón en rama sin desmotar',                                                           15.00, 'BIEN', 0),
('004', 'Caña de azúcar',                                                                         10.00, 'BIEN', 0),
('005', 'Madera',                                                                                  4.00, 'BIEN', 0),
('006', 'Arena y piedra',                                                                         10.00, 'BIEN', 0),
('007', 'Residuos, subproductos, desechos, recortes, desperdicios y formas primarias derivadas de los mismos', 15.00, 'BIEN', 0),
('008', 'Recursos hidrobiológicos',                                                                4.00, 'BIEN', 0),
('009', 'Bienes del inciso A) del Apéndice I de la Ley del IGV',                                  4.00, 'BIEN', 0),
('010', 'Carne y despojos comestibles',                                                            4.00, 'BIEN', 0),
('011', 'Aceite de pescado',                                                                      10.00, 'BIEN', 0),
('012', 'Harina, polvo y "pellets" de pescado, crustáceos, moluscos y demás invertebrados acuáticos', 4.00, 'BIEN', 0),
('013', 'Embarcaciones pesqueras',                                                                 9.00, 'BIEN', 0),
('014', 'Leche',                                                                                   4.00, 'BIEN', 0),
('015', 'Maíz amarillo duro',                                                                      4.00, 'BIEN', 0),
('016', 'Trigo y morcajo (tranquillón)',                                                            7.00, 'BIEN', 0),
('017', 'Bigas sin refuerzo o preesforzadas',                                                     10.00, 'BIEN', 0),
('018', 'Minerales metálicos no auríferos',                                                       10.00, 'BIEN', 0);

-- ─────────────────────────────────────────────────────────────────────────────
-- SERVICIOS sujetos al SPOT (Anexo 2 de la RS 183-2004/SUNAT)
-- Tasas actualizadas por RS N° 071-2018/SUNAT (vigentes desde 01/04/2018)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO detraction_code (code, description, percentage, category, status) VALUES
('019', 'Arrendamiento de bienes',                                                               10.00, 'SERVICIO', 1),
('020', 'Mantenimiento y reparación de bienes muebles',                                          12.00, 'SERVICIO', 1),
('021', 'Movimiento de carga',                                                                    4.00, 'SERVICIO', 0),
('022', 'Otros servicios empresariales',                                                         12.00, 'SERVICIO', 1),
('023', 'Comisión mercantil',                                                                    10.00, 'SERVICIO', 0),
('024', 'Fabricación de bienes por encargo',                                                     10.00, 'SERVICIO', 0),
('025', 'Servicio de transporte de personas',                                                    10.00, 'SERVICIO', 0),
('026', 'Contratos de construcción',                                                              4.00, 'SERVICIO', 0),
('027', 'Servicios de intermediación laboral y tercerización',                                   12.00, 'SERVICIO', 0),
('028', 'Arrendamiento de bienes inmuebles',                                                     10.00, 'SERVICIO', 0),
('029', 'Exploración, desarrollo y/o explotación de recursos naturales',                         15.00, 'SERVICIO', 0);

-- ─────────────────────────────────────────────────────────────────────────────
-- BIENES adicionales (Anexo 1 - adiciones posteriores)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO detraction_code (code, description, percentage, category, status) VALUES
('031', 'Oro y demás minerales metálicos exonerados del IGV',                                    10.00, 'BIEN', 0),
('032', 'Páprika y otros frutos de los géneros capsicum o pimienta',                             10.00, 'BIEN', 0),
('033', 'Espárragos',                                                                             9.00, 'BIEN', 0),
('034', 'Minerales metálicos auríferos',                                                         12.00, 'BIEN', 0),
('035', 'Bienes del inciso B) del Apéndice I de la Ley del IGV',                                  9.00, 'BIEN', 0),
('036', 'Aceite de oliva y sus fracciones',                                                      12.00, 'BIEN', 0);

-- ─────────────────────────────────────────────────────────────────────────────
-- SERVICIOS adicionales
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO detraction_code (code, description, percentage, category, status) VALUES
('037', 'Demás servicios gravados con el IGV',                                                   12.00, 'SERVICIO', 1);
