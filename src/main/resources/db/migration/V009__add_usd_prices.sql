-- Producto: renombrar columnas a _pen y agregar columnas _usd
ALTER TABLE product
  CHANGE COLUMN sale_price       sale_price_pen      DECIMAL(12, 2) NULL,
  CHANGE COLUMN estimated_cost   estimated_cost_pen  DECIMAL(12, 2) NULL,
  ADD    COLUMN sale_price_usd   DECIMAL(12, 2)      NULL,
  ADD    COLUMN estimated_cost_usd DECIMAL(12, 2)    NULL;

-- Servicio: renombrar price a price_pen y agregar price_usd
ALTER TABLE service
  CHANGE COLUMN price      price_pen DECIMAL(12, 2) NULL,
  ADD    COLUMN price_usd  DECIMAL(12, 2) NULL;
