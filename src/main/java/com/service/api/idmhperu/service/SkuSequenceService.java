package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.response.ApiResponse;

public interface SkuSequenceService {
  ApiResponse<String> generateSkuPreview(String type);

  String registerSku(String type);
}
