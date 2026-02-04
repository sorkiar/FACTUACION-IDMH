package com.service.api.idmhperu.dto.filter;

import lombok.Data;

@Data
public class ProductFilter {
  private Long id;
  private Integer status;
  private Long categoryId;
  private String sku;
}
