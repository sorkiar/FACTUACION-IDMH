package com.service.api.idmhperu.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class SaleResponse {
  private Long id;
  private String saleStatus;
  private String currencyCode;
  private String paymentType;
  private String purchaseOrder;
  private String observations;
  private BigDecimal subtotalAmount;
  private BigDecimal taxAmount;
  private BigDecimal totalAmount;
  private List<String> relatedGuides;
  private ClientResponse client;
  private List<SaleItemResponse> items;
  private List<SalePaymentResponse> payments;
  private List<SaleInstallmentResponse> installments;
  private DocumentResponse document;
}
