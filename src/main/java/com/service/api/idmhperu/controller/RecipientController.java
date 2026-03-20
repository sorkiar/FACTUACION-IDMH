package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.filter.RecipientFilter;
import com.service.api.idmhperu.dto.request.RecipientRequest;
import com.service.api.idmhperu.dto.request.RecipientStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.RecipientResponse;
import com.service.api.idmhperu.service.RecipientService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/recipients")
@RequiredArgsConstructor
public class RecipientController {

  private final RecipientService service;

  @GetMapping
  public ApiResponse<List<RecipientResponse>> list(
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) String docNumber,
      @RequestParam(required = false) String name
  ) {
    RecipientFilter filter = new RecipientFilter();
    filter.setStatus(status);
    filter.setDocNumber(docNumber);
    filter.setName(name);
    return service.findAll(filter);
  }

  @GetMapping("/{id}")
  public ApiResponse<RecipientResponse> findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PostMapping
  public ApiResponse<RecipientResponse> create(
      @RequestBody @Valid RecipientRequest request
  ) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public ApiResponse<RecipientResponse> update(
      @PathVariable Long id,
      @RequestBody @Valid RecipientRequest request
  ) {
    return service.update(id, request);
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<Void> updateStatus(
      @PathVariable Long id,
      @Valid @RequestBody RecipientStatusRequest request
  ) {
    return service.updateStatus(id, request);
  }
}
