package com.service.api.idmhperu.dto.external.apiperu;

import lombok.Data;

@Data
public class ExternalApiResponse<T> {

  private Boolean success;
  private T data;
  private String message;
  private Double time;
}
