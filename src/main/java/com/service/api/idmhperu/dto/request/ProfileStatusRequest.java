package com.service.api.idmhperu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProfileStatusRequest {

  @NotNull(message = "El estado es obligatorio")
  private Integer status;
}
