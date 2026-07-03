-- Ensure show_in_sales_report column exists (in case V042 did not apply)
SET @db = DATABASE();
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'document_type_sunat' AND COLUMN_NAME = 'show_in_sales_report');
SET @ddl = IF(@col_exists = 0,
    'ALTER TABLE document_type_sunat ADD COLUMN show_in_sales_report TINYINT(1) NOT NULL DEFAULT 0 AFTER status',
    'SELECT 1');
PREPARE s FROM @ddl;
EXECUTE s;
DEALLOCATE PREPARE s;

-- Enable for sales doc types
UPDATE document_type_sunat SET show_in_sales_report = 1 WHERE code IN ('01', '03');

-- Enable for credit/debit note types
UPDATE document_type_sunat SET show_in_sales_report = 1 WHERE code IN ('07', '08');
