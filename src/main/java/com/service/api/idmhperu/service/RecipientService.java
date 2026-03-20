package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.RecipientFilter;
import com.service.api.idmhperu.dto.request.RecipientRequest;
import com.service.api.idmhperu.dto.request.RecipientStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.RecipientResponse;
import java.util.List;

public interface RecipientService {

  ApiResponse<List<RecipientResponse>> findAll(RecipientFilter filter);

  ApiResponse<RecipientResponse> findById(Long id);

  ApiResponse<RecipientResponse> create(RecipientRequest request);

  ApiResponse<RecipientResponse> update(Long id, RecipientRequest request);

  ApiResponse<Void> updateStatus(Long id, RecipientStatusRequest request);
}
