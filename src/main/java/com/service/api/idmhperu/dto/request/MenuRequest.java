package com.service.api.idmhperu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuRequest {

  @NotBlank(message = "El nombre es obligatorio")
  private String name;

  private String path;

  private Long parentId;

  @NotNull(message = "El orden es obligatorio")
  private Integer sortOrder;
}
