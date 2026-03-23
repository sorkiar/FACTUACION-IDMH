package com.service.api.idmhperu.dto.filter;

import lombok.Data;

@Data
public class DetractionCodeFilter {
  private String code;
  private String category;
  private Integer status;
}
