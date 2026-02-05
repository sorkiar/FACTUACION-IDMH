package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.ProductFilter;
import com.service.api.idmhperu.dto.request.ProductRequest;
import com.service.api.idmhperu.dto.request.ProductStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ProductResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
  ApiResponse<List<ProductResponse>> findAll(ProductFilter filter);

  ApiResponse<ProductResponse> create(ProductRequest request, MultipartFile mainImage, MultipartFile technicalSheet);

  ApiResponse<ProductResponse> update(Long id, ProductRequest request, MultipartFile mainImage, MultipartFile technicalSheet);

  ApiResponse<Void> updateStatus(Long id, ProductStatusRequest request);
}
