package com.service.api.idmhperu.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesReportRowResponse {
  private String issueDate;
  private String document;
  private String documentTypeCode;
  private String documentTypeName;
  private String sunatStatus;
  private String client;
  private String itemDescription;
  private BigDecimal quantity;
  private BigDecimal unitPrice;
  private BigDecimal discountPercentage;
  private String currencyCode;
  private BigDecimal itemBaseAmount;
  private BigDecimal itemTaxAmount;
  private BigDecimal itemTotalAmount;
}
