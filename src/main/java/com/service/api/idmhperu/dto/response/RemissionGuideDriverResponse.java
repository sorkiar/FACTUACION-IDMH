package com.service.api.idmhperu.dto.response;

import lombok.Data;

@Data
public class RemissionGuideDriverResponse {

  private Long id;
  private String driverDocType;
  private String driverDocNumber;
  private String driverFirstName;
  private String driverLastName;
  private String driverLicenseNumber;
  private String vehiclePlate;
}
