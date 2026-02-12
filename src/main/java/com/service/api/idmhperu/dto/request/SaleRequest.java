package com.service.api.idmhperu.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class SaleRequest {
  private Long clientId;

  @Valid
  @NotEmpty
  private List<SaleItemRequest> items;

  // Si es borrador no es obligatorio
  private List<SalePaymentRequest> payments;

  private Boolean draft = false;

  // Documento solo si no es borrador
  private String documentTypeCode;
  private Long documentSeriesId;
}
