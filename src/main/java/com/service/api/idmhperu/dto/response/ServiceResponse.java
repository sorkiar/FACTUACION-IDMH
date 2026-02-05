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
  private String expectedDelivery;
  private Boolean requiresMaterials;
  private Boolean requiresSpecification;
  private String includesDescription;
  private String excludesDescription;
  private String conditions;
  private String shortDescription;
  private String detailedDescription;
  private String imageUrl;
  private String technicalSheetUrl;
  private Integer status;
}
