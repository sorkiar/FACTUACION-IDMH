package com.service.api.idmhperu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecipientRequest {

  @NotBlank(message = "docType es obligatorio")
  private String docType;

  @NotBlank(message = "docNumber es obligatorio")
  private String docNumber;

  @NotBlank(message = "name es obligatorio")
  private String name;

  private String address;
}
