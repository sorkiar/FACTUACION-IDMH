package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.UnitMeasure;
import com.service.api.idmhperu.dto.response.UnitMeasureResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnitMeasureMapper {
  UnitMeasureResponse toResponse(UnitMeasure entity);
  List<UnitMeasureResponse> toResponseList(List<UnitMeasure> entities);
}
