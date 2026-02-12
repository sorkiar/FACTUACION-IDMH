package com.service.api.idmhperu.dto.filter;

import lombok.Data;

@Data
public class PaymentMethodFilter {
  private Integer id;
  private Integer status;
  private String code;
}
