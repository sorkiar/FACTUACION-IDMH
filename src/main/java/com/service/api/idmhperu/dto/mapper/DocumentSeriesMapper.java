package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.DocumentSeries;
import com.service.api.idmhperu.dto.response.DocumentSeriesResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DocumentSeriesMapper {

  @Mapping(source = "documentTypeSunat.code", target = "documentTypeSunatCode")
  @Mapping(source = "documentTypeSunat.name", target = "documentTypeSunatName")
  @Mapping(source = "currentSequence", target = "sequence")
  DocumentSeriesResponse toResponse(DocumentSeries entity);
}
