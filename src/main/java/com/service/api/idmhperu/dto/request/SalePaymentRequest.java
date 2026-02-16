package com.service.api.idmhperu.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class SalePaymentRequest {

  @NotNull
  private Integer paymentMethodId;

  @NotNull
  private BigDecimal amountPaid;

  private String paymentReference;

  private String proofKey; // clave para asociar archivo
}
