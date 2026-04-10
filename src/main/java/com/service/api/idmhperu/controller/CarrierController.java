package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.filter.CarrierFilter;
import com.service.api.idmhperu.dto.request.CarrierRequest;
import com.service.api.idmhperu.dto.request.CarrierStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.CarrierResponse;
import com.service.api.idmhperu.service.CarrierService;
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
@RequestMapping("/api/carriers")
@RequiredArgsConstructor
@Validated
public class CarrierController {

  private final CarrierService service;

  @GetMapping
  public ApiResponse<List<CarrierResponse>> list(
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) Integer status
  ) {
    CarrierFilter filter = new CarrierFilter();
    filter.setId(id);
    filter.setStatus(status);
    return service.findAll(filter);
  }

  @GetMapping("/{id}")
  public ApiResponse<CarrierResponse> findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  public ApiResponse<CarrierResponse> create(@Valid @RequestBody CarrierRequest request) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public ApiResponse<CarrierResponse> update(
      @PathVariable Long id,
      @Valid @RequestBody CarrierRequest request
  ) {
    return service.update(id, request);
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<Void> updateStatus(
      @PathVariable Long id,
      @Valid @RequestBody CarrierStatusRequest request
  ) {
    return service.updateStatus(id, request);
  }
}
