-- Add exchange_rate to sale and credit_debit_note (idempotent)
-- PEN transactions store 1.0000; USD transactions store the type-"V" TC from the transaction date

SET @db = DATABASE();

-- sale: add column only if missing
SET @exists_sale = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'sale' AND COLUMN_NAME = 'exchange_rate');
SET @ddl = IF(@exists_sale = 0,
    'ALTER TABLE sale ADD COLUMN exchange_rate DECIMAL(10,4) NULL AFTER currency_code',
    'SELECT 1');
PREPARE s FROM @ddl;
EXECUTE s;
DEALLOCATE PREPARE s;

-- credit_debit_note: add column only if missing
SET @exists_note = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'credit_debit_note' AND COLUMN_NAME = 'exchange_rate');
SET @ddl = IF(@exists_note = 0,
    'ALTER TABLE credit_debit_note ADD COLUMN exchange_rate DECIMAL(10,4) NULL AFTER currency_code',
    'SELECT 1');
PREPARE s FROM @ddl;
EXECUTE s;
DEALLOCATE PREPARE s;

-- Populate sale: PEN → 1
UPDATE sale
SET exchange_rate = 1.0000
WHERE currency_code = 'PEN';

-- Populate sale: USD → TC type "V" from sale creation date
UPDATE sale s
SET s.exchange_rate = (
    SELECT er.value
    FROM exchange_rate er
    WHERE er.type = 'V'
      AND er.rate_date = DATE(s.created_at)
    ORDER BY er.rate_date DESC
    LIMIT 1
)
WHERE s.currency_code = 'USD';

-- Fallback: USD sales with no TC record for their date → 1
UPDATE sale
SET exchange_rate = 1.0000
WHERE currency_code = 'USD'
  AND exchange_rate IS NULL;

-- Populate credit_debit_note: inherit exchange_rate from the associated sale
UPDATE credit_debit_note cdn
    JOIN sale s ON s.id = cdn.sale_id
SET cdn.exchange_rate = s.exchange_rate
WHERE cdn.exchange_rate IS NULL;
