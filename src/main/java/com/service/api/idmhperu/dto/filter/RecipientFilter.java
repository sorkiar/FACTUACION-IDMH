package com.service.api.idmhperu.dto.filter;

import lombok.Data;

@Data
public class RecipientFilter {
  private Integer status;
  private String docNumber;
  private String name;
}
