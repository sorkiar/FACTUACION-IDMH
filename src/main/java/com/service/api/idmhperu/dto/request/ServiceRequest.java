package com.service.api.idmhperu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ServiceRequest {
  @NotBlank(message = "El SKU es obligatorio")
  private String sku;

  @NotBlank(message = "El nombre es obligatorio")
  private String name;

  @NotNull(message = "La categoría es obligatoria")
  private Long serviceCategoryId;

  @NotNull(message = "La unidad de cobro es obligatoria")
  private Long chargeUnitId;

  private BigDecimal price;
  private String estimatedTime;
  private String expectedDeliverable;

  private String includes;
  private String excludes;
  private String conditions;

  @NotNull(message = "Indicar si requiere materiales es obligatorio")
  private Integer requiresMaterials;

  @NotNull(message = "Indicar si requiere plano es obligatorio")
  private Integer requiresPlan;

  @NotBlank(message = "La descripción corta es obligatoria")
  private String shortDescription;

  @NotBlank(message = "La descripción detallada es obligatoria")
  private String detailedDescription;
}
