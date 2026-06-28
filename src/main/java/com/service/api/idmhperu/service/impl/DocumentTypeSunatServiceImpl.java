package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.filter.DocumentTypeSunatFilter;
import com.service.api.idmhperu.dto.mapper.DocumentTypeSunatMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DocumentTypeSunatResponse;
import com.service.api.idmhperu.repository.DocumentTypeSunatRepository;
import com.service.api.idmhperu.repository.spec.DocumentTypeSunatSpecification;
import com.service.api.idmhperu.service.DocumentTypeSunatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentTypeSunatServiceImpl implements DocumentTypeSunatService {

  private final DocumentTypeSunatRepository repository;
  private final DocumentTypeSunatMapper mapper;

  @Override
  public ApiResponse<List<DocumentTypeSunatResponse>> findAll(DocumentTypeSunatFilter filter) {
    return new ApiResponse<>(
        "Tipos de documento SUNAT listados correctamente",
        mapper.toResponseList(
            repository.findAll(DocumentTypeSunatSpecification.byFilter(filter))
        )
    );
  }
}
