package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.filter.DocumentTypeSunatFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DocumentTypeSunatResponse;
import com.service.api.idmhperu.service.DocumentTypeSunatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sunat-document-types")
@RequiredArgsConstructor
public class DocumentTypeSunatController {

  private final DocumentTypeSunatService service;

  @GetMapping
  public ApiResponse<List<DocumentTypeSunatResponse>> list(
      @RequestParam(required = false) String code,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) Boolean showInSalesReport
  ) {
    DocumentTypeSunatFilter filter = new DocumentTypeSunatFilter();
    filter.setCode(code);
    filter.setStatus(status);
    filter.setShowInSalesReport(showInSalesReport);
    return service.findAll(filter);
  }
}
