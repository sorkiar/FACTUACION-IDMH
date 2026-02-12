package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.filter.SaleFilter;
import com.service.api.idmhperu.dto.request.SaleRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.SaleResponse;
import com.service.api.idmhperu.service.SaleService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Validated
public class SaleController {

  private final SaleService service;

  @GetMapping
  public ApiResponse<List<SaleResponse>> list(
      @RequestParam(required = false) Long id,
      @RequestParam(required = false) Long clientId,
      @RequestParam(required = false) String saleStatus,
      @RequestParam(required = false) String paymentStatus,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
  ) {

    SaleFilter filter = new SaleFilter();
    filter.setId(id);
    filter.setClientId(clientId);
    filter.setSaleStatus(saleStatus);
    filter.setPaymentStatus(paymentStatus);
    filter.setStartDate(startDate);
    filter.setEndDate(endDate);

    return service.findAll(filter);
  }

  @PostMapping
  public ApiResponse<SaleResponse> create(
      @Valid @RequestBody SaleRequest request
  ) {
    return service.create(request);
  }
}
