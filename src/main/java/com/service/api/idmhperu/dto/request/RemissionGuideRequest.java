package com.service.api.idmhperu.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class RemissionGuideRequest {
  /** Fecha de inicio del traslado */
  @NotNull(message = "transferDate es obligatorio")
  private LocalDate transferDate;

  /**
   * Catálogo 20 SUNAT: VENTA, COMPRA, TRASLADO_EMPRESA, OTROS, EXPORTACION,
   * TRASLADO_ITINERANTE, etc.
   */
  @NotBlank(message = "transferReason es obligatorio")
  private String transferReason;

  /** Descripción libre. Obligatoria cuando transferReason = OTROS. */
  private String transferReasonDescription;

  /**
   * Catálogo 18 SUNAT: TRANSPORTE_PUBLICO | TRANSPORTE_PRIVADO
   */
  @NotBlank(message = "transportMode es obligatorio")
  private String transportMode;

  @NotNull(message = "grossWeight es obligatorio")
  @DecimalMin(value = "0.001", message = "grossWeight debe ser mayor a 0")
  private BigDecimal grossWeight;

  /** Unidad de medida del peso. Default KGM. */
  private String weightUnit = "KGM";

  /** Número de bultos. Default 1. */
  private Integer packageCount = 1;

  // Punto de partida
  @NotBlank(message = "originAddress es obligatorio")
  private String originAddress;

  @NotBlank(message = "originUbigeo es obligatorio")
  private String originUbigeo;

  private String originLocalCode;

  // Punto de llegada
  @NotBlank(message = "destinationAddress es obligatorio")
  private String destinationAddress;

  @NotBlank(message = "destinationUbigeo es obligatorio")
  private String destinationUbigeo;

  private String destinationLocalCode;

  private Boolean minorVehicleTransfer = false;

  // Destinatario
  @NotBlank(message = "recipientDocType es obligatorio")
  private String recipientDocType;

  @NotBlank(message = "recipientDocNumber es obligatorio")
  private String recipientDocNumber;

  @NotBlank(message = "recipientName es obligatorio")
  private String recipientName;

  private String recipientAddress;

  // Transportista (requerido solo si transportMode = TRANSPORTE_PUBLICO)
  private String carrierDocType;
  private String carrierDocNumber;
  private String carrierName;

  private String observations;

  @NotEmpty(message = "Debe registrar al menos un ítem")
  @Valid
  private List<RemissionGuideItemRequest> items;

  /** Requerido si transportMode = TRANSPORTE_PRIVADO */
  @Valid
  private List<RemissionGuideDriverRequest> drivers;
}
