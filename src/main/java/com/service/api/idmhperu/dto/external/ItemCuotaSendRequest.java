package com.service.api.idmhperu.dto.external;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ItemCuotaSendRequest {

  /** Número de cuota (empieza en 1). */
  private Integer itcuCuota;

  /** Fecha de vencimiento en milisegundos (timestamp UTC). */
  private Long itcuFecha;

  /** Monto de la cuota. */
  private BigDecimal itcuMonto;
}
