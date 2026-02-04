package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.ServiceFilter;
import com.service.api.idmhperu.dto.request.ServiceRequest;
import com.service.api.idmhperu.dto.request.ServiceStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ServiceResponse;
import java.util.List;

public interface ServiceService {
  ApiResponse<List<ServiceResponse>> findAll(ServiceFilter filter);

  ApiResponse<ServiceResponse> create(ServiceRequest request);

  ApiResponse<ServiceResponse> update(Long id, ServiceRequest request);

  ApiResponse<Void> updateStatus(Long id, ServiceStatusRequest request);
}
