package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.filter.DocumentTypeFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DocumentTypeResponse;
import com.service.api.idmhperu.service.DocumentTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/document-types")
@RequiredArgsConstructor
public class DocumentTypeController {
  private final DocumentTypeService service;

  @GetMapping
  public ApiResponse<List<DocumentTypeResponse>> list(
      @RequestParam(required = false) Integer status
  ) {
    DocumentTypeFilter filter = new DocumentTypeFilter();
    filter.setStatus(status);

    return service.findAll(filter);
  }
}
