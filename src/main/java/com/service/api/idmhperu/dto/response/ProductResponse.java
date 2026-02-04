package com.service.api.idmhperu.dto.response;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductResponse {
  private Long id;
  private String sku;
  private String name;

  private Long categoryId;
  private String categoryName;

  private Long unitMeasureId;
  private String unitMeasureCode;

  private BigDecimal salePrice;
  private BigDecimal estimatedCost;

  private String brand;
  private String model;

  private String shortDescription;
  private String technicalSpec;

  private String mainImageUrl;
  private String technicalSheetUrl;

  private Integer status;
}
