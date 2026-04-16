package com.service.api.idmhperu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
  @NotBlank(message = "La nueva contraseña es obligatoria")
  private String newPassword;
}
