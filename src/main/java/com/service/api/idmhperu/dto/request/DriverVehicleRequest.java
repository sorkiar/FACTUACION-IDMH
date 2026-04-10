package com.service.api.idmhperu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DriverVehicleRequest {

  @NotBlank(message = "plate es obligatorio")
  private String plate;
}
