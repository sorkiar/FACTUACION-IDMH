package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.filter.TransferReasonFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.TransferReasonResponse;
import com.service.api.idmhperu.service.TransferReasonService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfer-reasons")
@RequiredArgsConstructor
public class TransferReasonController {

  private final TransferReasonService service;

  @GetMapping
  public ApiResponse<List<TransferReasonResponse>> list(
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) Integer status
  ) {
    TransferReasonFilter filter = new TransferReasonFilter();
    filter.setId(id);
    filter.setStatus(status);
    return service.findAll(filter);
  }
}
