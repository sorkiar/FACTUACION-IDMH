package com.service.api.idmhperu.dto.response;

import lombok.Data;

@Data
public class DocumentTypeResponse {
  private Long id;
  private String name;
  private Integer length;
  private String description;
  private Integer status;
}
