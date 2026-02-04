package com.service.api.idmhperu.dto.response;

import lombok.Data;

@Data
public class ClientResponse {
  private Long id;
  private String personType;
  private String documentType;
  private String documentNumber;
  private String firstName;
  private String lastName;
  private String businessName;
  private String phone1;
  private String email1;
  private String address;
  private Integer status;
}
