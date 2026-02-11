package com.service.api.idmhperu.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductRequest {
  @NotBlank(message = "El nombre es obligatorio")
  private String name;

  @NotNull(message = "La categoría es obligatoria")
  private Long categoryId;

  @NotNull(message = "La unidad de medida es obligatoria")
  private Long unitMeasureId;

  @NotNull(message = "El precio de venta es obligatorio")
  @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
  private BigDecimal salePrice;

  @DecimalMin(value = "0.00", message = "El costo no puede ser negativo")
  private BigDecimal estimatedCost;

  private String brand;
  private String model;

  @NotBlank(message = "La descripción corta es obligatoria")
  private String shortDescription;

  @NotBlank(message = "La especificación técnica es obligatoria")
  private String technicalSpec;
}
