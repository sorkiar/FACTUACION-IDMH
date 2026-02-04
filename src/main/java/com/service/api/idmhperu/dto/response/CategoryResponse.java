package com.service.api.idmhperu.dto.response;

import lombok.Data;

@Data
public class CategoryResponse {
  private Long id;
  private String name;
  private String description;
  private Integer status;
}
