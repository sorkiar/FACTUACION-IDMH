package com.service.api.idmhperu.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SalePaymentResponse {
  private Long id;
  private PaymentMethodResponse paymentMethod;
  private BigDecimal amountPaid;
  private BigDecimal changeAmount;
  private LocalDateTime paymentDate;
  private String paymentReference;
  private String proofFileUrl;
  private String observations;
}
