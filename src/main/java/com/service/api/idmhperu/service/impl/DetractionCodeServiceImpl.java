package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.filter.DetractionCodeFilter;
import com.service.api.idmhperu.dto.mapper.DetractionCodeMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DetractionCodeResponse;
import com.service.api.idmhperu.repository.DetractionCodeRepository;
import com.service.api.idmhperu.repository.spec.DetractionCodeSpecification;
import com.service.api.idmhperu.service.DetractionCodeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetractionCodeServiceImpl implements DetractionCodeService {

  private final DetractionCodeRepository repository;
  private final DetractionCodeMapper mapper;

  @Override
  public ApiResponse<List<DetractionCodeResponse>> findAll(DetractionCodeFilter filter) {
    return new ApiResponse<>(
        "Códigos de detracción listados correctamente",
        mapper.toResponseList(
            repository.findAll(DetractionCodeSpecification.byFilter(filter))
        )
    );
  }
}
