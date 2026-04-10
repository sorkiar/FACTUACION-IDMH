package com.service.api.idmhperu.dto.response;

import lombok.Data;

@Data
public class ClientAddressResponse {

  private Long id;
  private String address;
  private String ubigeo;
  private String description;
}
