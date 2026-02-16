package com.service.api.idmhperu.dto.external;

import lombok.Data;

@Data
public class SunatSendRequest {
  private CompanySendRequest empresa;
  private DocumentSendRequest comprobante;
}
