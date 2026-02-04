package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.ProductFilter;
import com.service.api.idmhperu.dto.request.ProductRequest;
import com.service.api.idmhperu.dto.request.ProductStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ProductResponse;
import java.util.List;

public interface ProductService {
  ApiResponse<List<ProductResponse>> findAll(ProductFilter filter);

  ApiResponse<ProductResponse> create(ProductRequest request);

  ApiResponse<ProductResponse> update(Long id, ProductRequest request);

  ApiResponse<Void> updateStatus(Long id, ProductStatusRequest request);
}
