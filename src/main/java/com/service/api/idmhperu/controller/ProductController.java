package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.filter.ProductFilter;
import com.service.api.idmhperu.dto.request.ProductRequest;
import com.service.api.idmhperu.dto.request.ProductStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ProductResponse;
import com.service.api.idmhperu.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

  private final ProductService service;

  @GetMapping
  public ApiResponse<List<ProductResponse>> list(
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) String sku
  ) {
    ProductFilter filter = new ProductFilter();
    filter.setId(id);
    filter.setStatus(status);
    filter.setCategoryId(categoryId);
    filter.setSku(sku);

    return service.findAll(filter);
  }

  @PostMapping
  public ApiResponse<ProductResponse> create(
      @Valid @RequestBody ProductRequest request
  ) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public ApiResponse<ProductResponse> update(
      @PathVariable Long id,
      @Valid @RequestBody ProductRequest request
  ) {
    return service.update(id, request);
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<Void> updateStatus(
      @PathVariable Long id,
      @Valid @RequestBody ProductStatusRequest request
  ) {
    return service.updateStatus(id, request);
  }
}
