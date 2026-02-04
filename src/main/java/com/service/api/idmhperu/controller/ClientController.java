package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.filter.ClientFilter;
import com.service.api.idmhperu.dto.request.ClientRequest;
import com.service.api.idmhperu.dto.request.ClientStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ClientResponse;
import com.service.api.idmhperu.service.ClientService;
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
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Validated
public class ClientController {
  private final ClientService service;

  @GetMapping
  public ApiResponse<List<ClientResponse>> list(
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) Long documentTypeId,
      @RequestParam(required = false) String documentNumber
  ) {
    ClientFilter filter = new ClientFilter();
    filter.setId(id);
    filter.setStatus(status);
    filter.setDocumentTypeId(documentTypeId);
    filter.setDocumentNumber(documentNumber);

    return service.findAll(filter);
  }

  @PostMapping
  public ApiResponse<ClientResponse> create(
      @Valid @RequestBody ClientRequest request
  ) {
    return service.create(request);
  }

  @PutMapping("/{id}")
  public ApiResponse<ClientResponse> update(
      @PathVariable Long id,
      @Valid @RequestBody ClientRequest request
  ) {
    return service.update(id, request);
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<Void> updateStatus(
      @PathVariable Long id,
      @Valid @RequestBody ClientStatusRequest request
  ) {
    return service.updateStatus(id, request);
  }
}
