package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.filter.DocumentTypeFilter;
import com.service.api.idmhperu.dto.mapper.DocumentTypeMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DocumentTypeResponse;
import com.service.api.idmhperu.repository.DocumentTypeRepository;
import com.service.api.idmhperu.repository.spec.DocumentTypeSpecification;
import com.service.api.idmhperu.service.DocumentTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentTypeServiceImpl implements DocumentTypeService {
  private final DocumentTypeRepository repository;
  private final DocumentTypeMapper mapper;

  @Override
  public ApiResponse<List<DocumentTypeResponse>> findAll(DocumentTypeFilter filter) {
    return new ApiResponse<>("Tipos de documento listado correctamente",
        mapper.toResponseList(repository.findAll(DocumentTypeSpecification.byFilter(filter))));
  }
}
