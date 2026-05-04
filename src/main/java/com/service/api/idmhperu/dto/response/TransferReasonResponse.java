package com.service.api.idmhperu.dto.response;

import lombok.Data;

@Data
public class TransferReasonResponse {
  private Long id;
  private String code;
  private String name;
  private Integer status;
}
