package com.service.api.idmhperu.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class LineTotalsCalculator {

  private LineTotalsCalculator() {
  }

  public interface RoundableLine {
    BigDecimal getSubtotalAmount();

    BigDecimal getTaxAmount();

    void setTaxAmount(BigDecimal taxAmount);

    BigDecimal getTotalAmount();

    void setTotalAmount(BigDecimal totalAmount);
  }

  public record LineTotals(
      BigDecimal unroundedLineTotal,
      BigDecimal subtotalAmount,
      BigDecimal taxAmount,
      BigDecimal totalAmount,
      BigDecimal grossAmount) {
  }

  public static LineTotals compute(
      BigDecimal quantity, BigDecimal unitPrice, BigDecimal discountPercentage, BigDecimal taxRate) {

    BigDecimal discountPct = discountPercentage != null ? discountPercentage : BigDecimal.ZERO;
    BigDecimal hundred = new BigDecimal("100");

    // lineTotal = qty × unitPrice × (1 - disc/100) — sin redondeo intermedio
    BigDecimal lineTotal = quantity
        .multiply(unitPrice)
        .multiply(hundred.subtract(discountPct))
        .divide(hundred, 10, RoundingMode.HALF_UP);

    BigDecimal subtotalAmount = lineTotal.setScale(2, RoundingMode.HALF_UP);
    BigDecimal totalAmount = lineTotal.add(lineTotal.multiply(taxRate))
        .setScale(2, RoundingMode.HALF_UP);

    // taxAmount se deriva como diferencia (no se redondea por separado) para
    // garantizar subtotalAmount + taxAmount == totalAmount siempre, por construcción.
    BigDecimal taxAmount = totalAmount.subtract(subtotalAmount);

    BigDecimal grossAmount = quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);

    return new LineTotals(lineTotal, subtotalAmount, taxAmount, totalAmount, grossAmount);
  }

  public static <T extends RoundableLine> void absorbRoundingDiff(List<T> lines, BigDecimal targetTotal) {
    if (lines.isEmpty()) {
      return;
    }

    BigDecimal sumTotals = lines.stream()
        .map(RoundableLine::getTotalAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal diff = targetTotal.subtract(sumTotals);
    if (diff.compareTo(BigDecimal.ZERO) != 0) {
      T last = lines.get(lines.size() - 1);
      last.setTaxAmount(last.getTaxAmount().add(diff));
      last.setTotalAmount(last.getTotalAmount().add(diff));
    }
  }
}
