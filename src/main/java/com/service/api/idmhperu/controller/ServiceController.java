package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.filter.ServiceFilter;
import com.service.api.idmhperu.dto.request.ServiceRequest;
import com.service.api.idmhperu.dto.request.ServiceStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ServiceResponse;
import com.service.api.idmhperu.service.ServiceService;
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
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Validated
public class ServiceController {
  private final ServiceService service;

  @GetMapping
  public ApiResponse<List<ServiceResponse>> list(
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) Long serviceCategoryId,
      @RequestParam(required = false) String sku,
      @RequestParam(required = false) String name
  ) {
    ServiceFilter filter = new ServiceFilter();
    filter.setId(id);
    filter.setStatus(status);
    filter.setServiceCategoryId(serviceCategoryId);
    filter.setSku(sku);
    filter.setName(name);

    return service.findAll(filter);
  }

  @PostMapping
  public ApiResponse<ServiceResponse> create(
      @Valid @RequestBody ServiceRequest request
  ) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public ApiResponse<ServiceResponse> update(
      @PathVariable Long id,
      @Valid @RequestBody ServiceRequest request
  ) {
    return service.update(id, request);
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<Void> updateStatus(
      @PathVariable Long id,
      @Valid @RequestBody ServiceStatusRequest request
  ) {
    return service.updateStatus(id, request);
  }
}
