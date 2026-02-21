package com.service.api.idmhperu.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class RemissionGuideResponse {

  private Long id;
  private Long documentSeriesId;
  private String series;
  private String sequence;
  private LocalDateTime issueDate;
  private LocalDate transferDate;

  private String transferReason;
  private String transferReasonDescription;
  private String transportMode;

  private BigDecimal grossWeight;
  private String weightUnit;
  private Integer packageCount;

  // Punto de partida
  private String originAddress;
  private String originUbigeo;
  private String originLocalCode;

  // Punto de llegada
  private String destinationAddress;
  private String destinationUbigeo;
  private String destinationLocalCode;

  private Boolean minorVehicleTransfer;

  // Destinatario
  private String recipientDocType;
  private String recipientDocNumber;
  private String recipientName;
  private String recipientAddress;

  // Transportista (TRANSPORTE_PUBLICO)
  private String carrierDocType;
  private String carrierDocNumber;
  private String carrierName;

  private String observations;

  // Estado SUNAT
  private String status;
  private Integer sunatResponseCode;
  private String sunatMessage;
  private String hashCode;
  private String xmlUrl;
  private String cdrUrl;
  private String pdfUrl;

  private List<RemissionGuideItemResponse> items;
  private List<RemissionGuideDriverResponse> drivers;
}
