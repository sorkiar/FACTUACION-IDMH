package com.service.api.idmhperu.dto.response;

import lombok.Data;

@Data
public class RemissionGuideDriverResponse {

  private Long id;
  private DriverResponse driver;
  private DriverVehicleResponse driverVehicle;
}
