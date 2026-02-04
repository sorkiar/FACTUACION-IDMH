package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.filter.ChargeUnitFilter;
import com.service.api.idmhperu.dto.mapper.ChargeUnitMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ChargeUnitResponse;
import com.service.api.idmhperu.repository.ChargeUnitRepository;
import com.service.api.idmhperu.repository.spec.ChargeUnitSpecification;
import com.service.api.idmhperu.service.ChargeUnitService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChargeUnitServiceImpl implements ChargeUnitService {
  private final ChargeUnitRepository repository;
  private final ChargeUnitMapper mapper;

  @Override
  public ApiResponse<List<ChargeUnitResponse>> findAll(ChargeUnitFilter filter) {
    return new ApiResponse<>(
        "Unidades de cobro listadas correctamente",
        mapper.toResponseList(
            repository.findAll(ChargeUnitSpecification.byFilter(filter))
        )
    );
  }
}
