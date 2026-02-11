package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.service.SkuSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sku")
@RequiredArgsConstructor
public class SkuSequenceController {

  private final SkuSequenceService service;

  @GetMapping("/preview")
  public ApiResponse<String> previewSku(@RequestParam String type) {
    return service.generateSkuPreview(type);
  }
}
