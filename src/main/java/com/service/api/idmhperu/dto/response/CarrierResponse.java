package com.service.api.idmhperu.dto.response;

import lombok.Data;

@Data
public class CarrierResponse {

  private Long id;
  private String docType;
  private String docNumber;
  private String businessName;
  private Integer status;
}
