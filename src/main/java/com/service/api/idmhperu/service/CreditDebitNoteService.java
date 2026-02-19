package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.CreditDebitNoteFilter;
import com.service.api.idmhperu.dto.request.CreditDebitNoteRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.CreditDebitNoteResponse;
import java.util.List;

public interface CreditDebitNoteService {

  ApiResponse<List<CreditDebitNoteResponse>> findAll(CreditDebitNoteFilter filter);

  ApiResponse<CreditDebitNoteResponse> create(CreditDebitNoteRequest request);

  ApiResponse<CreditDebitNoteResponse> findById(Long id);
}
