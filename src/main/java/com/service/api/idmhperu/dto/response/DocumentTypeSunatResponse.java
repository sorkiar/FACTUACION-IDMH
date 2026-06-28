package com.service.api.idmhperu.dto.response;

import lombok.Data;

@Data
public class DocumentTypeSunatResponse {
  private String code;
  private String name;
  private Integer status;
  private Boolean showInSalesReport;
}
