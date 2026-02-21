package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.RemissionGuideFilter;
import com.service.api.idmhperu.dto.request.RemissionGuideRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.RemissionGuideResponse;
import java.util.List;

public interface RemissionGuideService {

  ApiResponse<List<RemissionGuideResponse>> findAll(RemissionGuideFilter filter);

  ApiResponse<RemissionGuideResponse> findById(Long id);

  ApiResponse<RemissionGuideResponse> create(RemissionGuideRequest request);
}
