-- sale_item: store gross_amount (qty × unit_price) and unit_price_with_tax
ALTER TABLE sale_item
  ADD COLUMN gross_amount DECIMAL(14,2) NULL AFTER total_amount,
  ADD COLUMN unit_price_with_tax DECIMAL(14,6) NULL AFTER gross_amount;

UPDATE sale_item
SET gross_amount       = ROUND(quantity * unit_price, 2),
    unit_price_with_tax = ROUND(unit_price * 1.18, 6);

-- credit_debit_note_item: store gross_amount, net_unit_price, net_unit_price_with_tax
ALTER TABLE credit_debit_note_item
  ADD COLUMN gross_amount            DECIMAL(14,2) NULL AFTER total_amount,
  ADD COLUMN net_unit_price          DECIMAL(14,6) NULL AFTER gross_amount,
  ADD COLUMN net_unit_price_with_tax DECIMAL(14,6) NULL AFTER net_unit_price;

UPDATE credit_debit_note_item
SET gross_amount            = ROUND(quantity * unit_price, 2),
    net_unit_price          = ROUND(subtotal_amount / NULLIF(quantity, 0), 6),
    net_unit_price_with_tax = ROUND(total_amount   / NULLIF(quantity, 0), 6);

-- sale: store retention_base_pen (total en PEN sobre el que se calculó la retención)
ALTER TABLE sale
  ADD COLUMN retention_base_pen DECIMAL(14,2) NULL AFTER retention_amount;

-- Para PEN: la base es simplemente total_amount (lo mismo que hace computeRetention en código)
UPDATE sale
SET retention_base_pen = total_amount
WHERE has_retention = 1
  AND currency_code = 'PEN'
  AND retention_amount IS NOT NULL;
-- USD históricos: se deja NULL (PDF usa fallback con TC del día de la venta)
