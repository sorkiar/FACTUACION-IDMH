package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.filter.UnitMeasureFilter;
import com.service.api.idmhperu.dto.mapper.UnitMeasureMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.UnitMeasureResponse;
import com.service.api.idmhperu.repository.UnitMeasureRepository;
import com.service.api.idmhperu.repository.spec.UnitMeasureSpecification;
import com.service.api.idmhperu.service.UnitMeasureService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitMeasureServiceImpl implements UnitMeasureService {
  private final UnitMeasureRepository repository;
  private final UnitMeasureMapper mapper;

  @Override
  public ApiResponse<List<UnitMeasureResponse>> findAll(UnitMeasureFilter filter) {
    return new ApiResponse<>(
        "Unidades de medida listadas correctamente",
        mapper.toResponseList(
            repository.findAll(UnitMeasureSpecification.byFilter(filter))
        )
    );
  }
}
