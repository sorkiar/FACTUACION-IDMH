package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.ChargeUnit;
import com.service.api.idmhperu.dto.response.ChargeUnitResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargeUnitMapper {
  ChargeUnitResponse toResponse(ChargeUnit entity);
  List<ChargeUnitResponse> toResponseList(List<ChargeUnit> entities);
}
