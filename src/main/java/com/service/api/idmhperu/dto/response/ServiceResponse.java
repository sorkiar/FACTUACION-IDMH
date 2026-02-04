package com.service.api.idmhperu.dto.response;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ServiceResponse {
  private Long id;
  private String sku;
  private String name;

  private Long serviceCategoryId;
  private String serviceCategoryName;

  private Long chargeUnitId;
  private String chargeUnitName;

  private BigDecimal price;
  private String estimatedTime;
  private String expectedDeliverable;

  private String includes;
  private String excludes;
  private String conditions;

  private Integer requiresMaterials;
  private Integer requiresPlan;

  private String shortDescription;
  private String detailedDescription;

  private Integer status;
}
