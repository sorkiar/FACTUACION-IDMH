package com.service.api.idmhperu.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceRequest {
  @NotBlank(message = "El nombre es obligatorio")
  private String name;

  @NotNull(message = "La categoría de servicio es obligatoria")
  private Long serviceCategoryId;

  @NotNull(message = "La unidad de cobro es obligatoria")
  private Long chargeUnitId;

  // Precio
  private BigDecimal price;

  // Tiempo / entrega
  private String estimatedTime;
  private String expectedDelivery;

  // Incluye / no incluye
  private String includesDescription;
  private String excludesDescription;
  private String conditions;

  // Flags
  @NotNull(message = "Indicar si requiere materiales es obligatorio")
  private Boolean requiresMaterials;

  @NotNull(message = "Indicar si requiere especificación es obligatorio")
  private Boolean requiresSpecification;

  // Descripción
  @NotBlank(message = "La descripción corta es obligatoria")
  private String shortDescription;

  @NotBlank(message = "La descripción detallada es obligatoria")
  private String detailedDescription;
}
