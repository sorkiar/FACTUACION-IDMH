package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.repository.SkuSequenceRepository;
import com.service.api.idmhperu.service.SkuSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkuSequenceServiceImpl implements SkuSequenceService {
  private final SkuSequenceRepository repository;

  @Override
  public ApiResponse<String> generateSkuPreview(String type) {
    int current = repository.getCurrentValue(type);
    String sku = String.format("%s%07d", type, current + 1);

    return new ApiResponse<>("Correlativo SKU listado correctamente", sku);
  }

  @Override
  public String registerSku(String type) {
    int next = repository.registerSku(type);

    return String.format("%s%07d", type, next);
  }
}
