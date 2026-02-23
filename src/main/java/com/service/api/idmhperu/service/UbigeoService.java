package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.UbigeoFilter;
import com.service.api.idmhperu.dto.request.UbigeoRequest;
import com.service.api.idmhperu.dto.request.UbigeoStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.UbigeoResponse;
import java.util.List;

public interface UbigeoService {

  ApiResponse<List<UbigeoResponse>> findAll(UbigeoFilter filter);

  ApiResponse<UbigeoResponse> findById(String ubigeo);

  ApiResponse<UbigeoResponse> create(UbigeoRequest request);

  ApiResponse<UbigeoResponse> update(String ubigeo, UbigeoRequest request);

  ApiResponse<Void> updateStatus(String ubigeo, UbigeoStatusRequest request);
}
