package com.service.api.idmhperu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CarrierRequest {

  /** Tipo de documento. Default RUC. */
  private String docType = "RUC";

  @NotBlank(message = "docNumber es obligatorio")
  private String docNumber;

  @NotBlank(message = "businessName es obligatorio")
  private String businessName;
}
