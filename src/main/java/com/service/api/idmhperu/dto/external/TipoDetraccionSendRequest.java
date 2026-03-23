package com.service.api.idmhperu.dto.external;

import lombok.Data;

@Data
public class TipoDetraccionSendRequest {
  private String tideCodigo;
  private String tideDescripcion;
  private Boolean tideEstado = true;
}
