package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.SaleFilter;
import com.service.api.idmhperu.dto.request.SaleRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.SaleResponse;
import java.util.List;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

public interface SaleService {

  ApiResponse<List<SaleResponse>> findAll(SaleFilter filter);

  ApiResponse<SaleResponse> create(SaleRequest request, MultiValueMap<String, MultipartFile> paymentProofs);

  ApiResponse<SaleResponse> updateDraft(Long id, SaleRequest request, MultiValueMap<String, MultipartFile> paymentProofs);

}
