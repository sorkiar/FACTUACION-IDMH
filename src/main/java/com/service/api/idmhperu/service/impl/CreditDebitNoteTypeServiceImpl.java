package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.CreditDebitNoteType;
import com.service.api.idmhperu.dto.filter.CreditDebitNoteTypeFilter;
import com.service.api.idmhperu.dto.mapper.CreditDebitNoteMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.CreditDebitNoteTypeResponse;
import com.service.api.idmhperu.repository.CreditDebitNoteTypeRepository;
import com.service.api.idmhperu.repository.spec.CreditDebitNoteTypeSpecification;
import com.service.api.idmhperu.service.CreditDebitNoteTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreditDebitNoteTypeServiceImpl implements CreditDebitNoteTypeService {

  private final CreditDebitNoteTypeRepository repository;
  private final CreditDebitNoteMapper mapper;

  @Override
  public ApiResponse<List<CreditDebitNoteTypeResponse>> findAll(CreditDebitNoteTypeFilter filter) {
    List<CreditDebitNoteType> types =
        repository.findAll(CreditDebitNoteTypeSpecification.byFilter(filter));
    List<CreditDebitNoteTypeResponse> response =
        types.stream().map(mapper::toTypeResponse).toList();
    return new ApiResponse<>("Tipos de nota listados correctamente", response);
  }
}
