package com.service.api.idmhperu.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class SaleResponse {
  private Long id;
  private Long clientId;
  private String clientName;
  private String saleStatus;
  private BigDecimal subtotalAmount;
  private BigDecimal taxAmount;
  private BigDecimal totalAmount;
  private String documentSeries;
  private String documentSequence;
  private List<SaleItemResponse> items;
  private DocumentResponse document;
}
