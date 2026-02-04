package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.DocumentType;
import com.service.api.idmhperu.dto.response.DocumentTypeResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentTypeMapper {
  DocumentTypeResponse toResponse(DocumentType entity);
  List<DocumentTypeResponse> toResponseList(List<DocumentType> entities);
}
