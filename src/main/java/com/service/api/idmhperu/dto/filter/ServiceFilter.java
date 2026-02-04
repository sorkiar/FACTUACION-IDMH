package com.service.api.idmhperu.dto.filter;

import lombok.Data;

@Data
public class ServiceFilter {
  private Long id;
  private Integer status;
  private Long serviceCategoryId;
  private String sku;
  private String name;
}
