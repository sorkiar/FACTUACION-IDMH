package com.service.api.idmhperu.dto.external;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ItemSendRequest {
  private String itcoUnidadMedida;
  private String itcoDescripcion;
  private BigDecimal itcoCantidad;
  private BigDecimal itcoValorUnitario;
  private BigDecimal itcoPrecioUnitario;
  private BigDecimal itcoSubTotal;
  private BigDecimal itcoIgv;
  private BigDecimal itcoTotal;
  private String tipoAfectacionIgv;
}
