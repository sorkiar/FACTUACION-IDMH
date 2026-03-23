package com.service.api.idmhperu.dto.response;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DetractionCodeResponse {
  private Long id;
  private String code;
  private String description;
  private BigDecimal percentage;
  private String category;
  private Integer status;
}
