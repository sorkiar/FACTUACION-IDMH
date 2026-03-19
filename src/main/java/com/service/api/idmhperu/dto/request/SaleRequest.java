package com.service.api.idmhperu.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class SaleRequest {
  @NotNull
  private Long clientId;

  @Valid
  @NotEmpty
  private List<SaleItemRequest> items;

  /** Moneda de la venta: PEN (soles) o USD (dólares). Por defecto PEN. */
  private String currencyCode = "PEN";

  /**
   * CONTADO o CREDITO. Por defecto CONTADO.
   * - CONTADO: pagos obligatorios, cuotas ignoradas.
   * - CREDITO: cuotas obligatorias, pagos no requeridos.
   */
  private String paymentType = "CONTADO";

  // Pagos: obligatorio si paymentType = CONTADO y no es borrador
  private List<SalePaymentRequest> payments;

  // Cuotas: obligatorio si paymentType = CREDITO y no es borrador
  @Valid
  private List<SaleInstallmentRequest> installments;

  private String purchaseOrder;

  private String observations;

  /** Guías de remisión relacionadas (números, p. ej. "T001-00000001"). */
  private List<String> relatedGuides;

  private Boolean draft = false;

  // Documento solo si no es borrador
  private String documentTypeCode;
  private Long documentSeriesId;
}
