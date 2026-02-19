package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.CreditDebitNoteTypeFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.CreditDebitNoteTypeResponse;
import java.util.List;

public interface CreditDebitNoteTypeService {
  ApiResponse<List<CreditDebitNoteTypeResponse>> findAll(CreditDebitNoteTypeFilter filter);
}
