package com.service.api.idmhperu.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RemissionGuideDriverRequest {

  /** ID del conductor en el maestro de conductores. */
  @NotNull(message = "driverId es obligatorio")
  private Long driverId;

  /** ID de la placa del conductor a usar en esta guía. */
  @NotNull(message = "vehiclePlateId es obligatorio")
  private Long vehiclePlateId;
}
