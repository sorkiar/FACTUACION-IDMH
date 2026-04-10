package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.RucRecord;
import com.service.api.idmhperu.dto.external.apiperu.ExternalRucData;
import com.service.api.idmhperu.dto.response.RucRecordResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RucRecordMapper {

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "ubigeoDept", expression = "java(extractUbigeo(data.getUbigeo(), 0))")
  @Mapping(target = "ubigeoProv", expression = "java(extractUbigeo(data.getUbigeo(), 1))")
  @Mapping(target = "ubigeoDist", expression = "java(extractUbigeo(data.getUbigeo(), 2))")
  RucRecord toEntity(ExternalRucData data);

  RucRecordResponse toResponse(RucRecord entity);

  default String extractUbigeo(String[] ubigeo, int index) {
    if (ubigeo == null || ubigeo.length <= index) return null;
    return ubigeo[index];
  }
}
