package com.service.api.idmhperu.dto.response;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ClientResponse {
  private Long id;
  private String personTypeId;
  private String personType;
  private String documentTypeId;
  private String documentType;
  private String documentNumber;
  private String firstName;
  private String lastName;
  private LocalDate birthDate;
  private String businessName;
  private String contactPersonName;
  private String phone1;
  private String email1;
  private String phone2;
  private String email2;
  private String address;
  private Integer status;
}
