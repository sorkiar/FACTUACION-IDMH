package com.service.api.idmhperu.dto.response;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class SaleItemResponse {
  private Long id;
  private String itemType;
  private String description;
  private BigDecimal quantity;
  private BigDecimal unitPrice;
  private BigDecimal subtotalAmount;
  private BigDecimal taxAmount;
  private BigDecimal totalAmount;
}
