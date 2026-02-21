package com.service.api.idmhperu.dto.external;

import lombok.Data;

/**
 * Datos del conductor y vehículo para TRANSPORTE_PRIVADO.
 * Corresponde al bean {@code GuiaTransporte} del proyecto facturador.
 */
@Data
public class GuiaTransporteSendRequest {

  /**
   * Nombre del enum {@code Catalog06TipoDocIdentidad} del facturador.
   * Ej: "DNI"
   */
  private String conductorTipoDocumento;

  private String conductorNumeroDocumento;
  private String conductorNombres;
  private String conductorApellidos;
  private String conductorNumeroLicencia;
  private String vehiculoPlaca;
}
