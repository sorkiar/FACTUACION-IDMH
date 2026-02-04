package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.filter.PersonTypeFilter;
import com.service.api.idmhperu.dto.mapper.PersonTypeMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.PersonTypeResponse;
import com.service.api.idmhperu.repository.PersonTypeRepository;
import com.service.api.idmhperu.repository.spec.PersonTypeSpecification;
import com.service.api.idmhperu.service.PersonTypeService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonTypeServiceImpl implements PersonTypeService {
  private final PersonTypeRepository repository;
  private final PersonTypeMapper mapper;

  @Override
  public ApiResponse<List<PersonTypeResponse>> findAll(PersonTypeFilter filter) {
    return new ApiResponse<>("Tipos de persona listado correctamente",
        mapper.toResponseList(repository.findAll(PersonTypeSpecification.byFilter(filter))));
  }
}
