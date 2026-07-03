-- 1. Redondear unit_price a 2 decimales (por si acaso vino con más precisión)
UPDATE sale_item
SET unit_price = ROUND(unit_price, 2)
WHERE deleted_at IS NULL;

-- 2. Recalcular unit_price_with_tax = ROUND(unit_price * 1.18, 2)
UPDATE sale_item
SET unit_price_with_tax = ROUND(unit_price * 1.18, 2)
WHERE deleted_at IS NULL;

-- 3. Recalcular tax_amount y total_amount por ítem desde subtotal_amount
UPDATE sale_item
SET tax_amount   = ROUND(subtotal_amount * 0.18, 2),
    total_amount = subtotal_amount + ROUND(subtotal_amount * 0.18, 2)
WHERE deleted_at IS NULL;

-- 4. Ajustar el último ítem (mayor id) de cada venta para que
--    sum(total_amount) = sale.total_amount exactamente (absorbe desfase de redondeo).
UPDATE sale_item si
INNER JOIN (
  SELECT
    last_si.id                       AS last_id,
    s.total_amount - sums.items_sum  AS diff
  FROM sale_item last_si
  INNER JOIN (
    SELECT sale_id, MAX(id) AS max_id
    FROM sale_item
    WHERE deleted_at IS NULL
    GROUP BY sale_id
  ) max_ids ON last_si.id = max_ids.max_id
  INNER JOIN (
    SELECT sale_id, SUM(total_amount) AS items_sum
    FROM sale_item
    WHERE deleted_at IS NULL
    GROUP BY sale_id
  ) sums ON last_si.sale_id = sums.sale_id
  INNER JOIN sale s ON s.id = last_si.sale_id
  WHERE s.total_amount <> sums.items_sum
) adj ON si.id = adj.last_id
SET
  si.total_amount = si.total_amount + adj.diff,
  si.tax_amount   = si.tax_amount   + adj.diff;
