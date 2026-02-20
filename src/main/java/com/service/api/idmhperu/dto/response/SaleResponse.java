package com.service.api.idmhperu.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class SaleResponse {
  private Long id;
  private String saleStatus;
  private BigDecimal subtotalAmount;
  private BigDecimal taxAmount;
  private BigDecimal totalAmount;
  private ClientResponse client;
  private List<SaleItemResponse> items;
  private List<SalePaymentResponse> payments;
  private DocumentResponse document;
}
