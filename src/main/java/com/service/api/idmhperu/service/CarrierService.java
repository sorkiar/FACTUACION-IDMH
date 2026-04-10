package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.CarrierFilter;
import com.service.api.idmhperu.dto.request.CarrierRequest;
import com.service.api.idmhperu.dto.request.CarrierStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.CarrierResponse;
import java.util.List;

public interface CarrierService {

  ApiResponse<List<CarrierResponse>> findAll(CarrierFilter filter);

  ApiResponse<CarrierResponse> findById(Long id);

  ApiResponse<CarrierResponse> create(CarrierRequest request);

  ApiResponse<CarrierResponse> update(Long id, CarrierRequest request);

  ApiResponse<Void> updateStatus(Long id, CarrierStatusRequest request);
}
