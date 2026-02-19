package com.service.api.idmhperu.dto.filter;

import lombok.Data;

@Data
public class CreditDebitNoteTypeFilter {
  private String code;
  private String noteCategory;
  private Integer status;
}
