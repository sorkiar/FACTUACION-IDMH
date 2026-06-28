ALTER TABLE document_type_sunat
    ADD COLUMN show_in_sales_report TINYINT(1) NOT NULL DEFAULT 0 AFTER status;

UPDATE document_type_sunat SET show_in_sales_report = 1 WHERE code IN ('01', '03');
