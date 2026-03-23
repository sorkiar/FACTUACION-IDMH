package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.DetractionCode;
import com.service.api.idmhperu.dto.response.DetractionCodeResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DetractionCodeMapper {
  DetractionCodeResponse toResponse(DetractionCode entity);
  List<DetractionCodeResponse> toResponseList(List<DetractionCode> entities);
}
