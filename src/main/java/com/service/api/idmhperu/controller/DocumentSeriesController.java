package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DocumentSeriesResponse;
import com.service.api.idmhperu.service.DocumentSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/document-series")
@RequiredArgsConstructor
public class DocumentSeriesController {

  private final DocumentSeriesService service;

  @GetMapping("/next-sequence")
  public ApiResponse<DocumentSeriesResponse> getNextSequence(
      @RequestParam String documentTypeCode
  ) {
    return service.getNextSequencePreview(documentTypeCode);
  }
}
