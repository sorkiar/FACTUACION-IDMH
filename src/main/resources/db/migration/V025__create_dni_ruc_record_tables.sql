CREATE TABLE dni_record (
    doc_number       VARCHAR(8)   NOT NULL,
    full_name        VARCHAR(200),
    first_name       VARCHAR(100),
    last_name_paternal VARCHAR(100),
    last_name_maternal VARCHAR(100),
    verification_code VARCHAR(10),
    address          VARCHAR(500),
    full_address     VARCHAR(500),
    ubigeo_reniec    VARCHAR(10),
    ubigeo_sunat     VARCHAR(10),
    ubigeo_dept      VARCHAR(10),
    ubigeo_prov      VARCHAR(10),
    ubigeo_dist      VARCHAR(10),
    created_at       DATETIME,
    created_by       VARCHAR(50)  NOT NULL DEFAULT 'system',
    updated_at       DATETIME,
    updated_by       VARCHAR(50),
    PRIMARY KEY (doc_number)
);

CREATE TABLE ruc_record (
    ruc                        VARCHAR(11)  NOT NULL,
    name                       VARCHAR(300),
    state                      VARCHAR(50),
    condition                  VARCHAR(50),
    address                    VARCHAR(500),
    full_address               VARCHAR(500),
    department                 VARCHAR(100),
    province                   VARCHAR(100),
    district                   VARCHAR(100),
    ubigeo_sunat               VARCHAR(10),
    ubigeo_dept                VARCHAR(10),
    ubigeo_prov                VARCHAR(10),
    ubigeo_dist                VARCHAR(10),
    is_retention_agent         TINYINT(1)   NOT NULL DEFAULT 0,
    is_perception_agent        TINYINT(1)   NOT NULL DEFAULT 0,
    is_perception_fuel_agent   TINYINT(1)   NOT NULL DEFAULT 0,
    is_good_taxpayer           TINYINT(1)   NOT NULL DEFAULT 0,
    created_at                 DATETIME,
    created_by                 VARCHAR(50)  NOT NULL DEFAULT 'system',
    updated_at                 DATETIME,
    updated_by                 VARCHAR(50),
    PRIMARY KEY (ruc)
);

INSERT INTO configuration (config_group, config_key, config_value, config_datatype, description, editable, created_by)
VALUES ('consulta_externa', 'apiperu_token', '3efee48759bbfae015316c6a137983a13a22b9e98632977e25489feb22153dcd', 'STRING', 'Token Bearer para API apiperu.dev', 1, 'system');
