package com.service.api.idmhperu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RemissionGuideDriverRequest {

  /**
   * Tipo de documento del conductor.
   * Usar nombre del enum Catalog06TipoDocIdentidad del facturador (ej: "DNI").
   */
  @NotBlank(message = "docType es obligatorio")
  private String docType;

  @NotBlank(message = "docNumber es obligatorio")
  private String docNumber;

  @NotBlank(message = "firstName es obligatorio")
  private String firstName;

  @NotBlank(message = "lastName es obligatorio")
  private String lastName;

  @NotBlank(message = "licenseNumber es obligatorio")
  private String licenseNumber;

  @NotBlank(message = "vehiclePlate es obligatorio")
  private String vehiclePlate;
}
