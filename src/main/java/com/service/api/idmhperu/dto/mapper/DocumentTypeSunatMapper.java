package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.DocumentTypeSunat;
import com.service.api.idmhperu.dto.response.DocumentTypeSunatResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentTypeSunatMapper {
  DocumentTypeSunatResponse toResponse(DocumentTypeSunat entity);
  List<DocumentTypeSunatResponse> toResponseList(List<DocumentTypeSunat> entities);
}
