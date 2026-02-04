package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.Service;
import com.service.api.idmhperu.dto.response.ServiceResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
  @Mapping(source = "serviceCategory.id", target = "serviceCategoryId")
  @Mapping(source = "serviceCategory.name", target = "serviceCategoryName")
  @Mapping(source = "chargeUnit.id", target = "chargeUnitId")
  @Mapping(source = "chargeUnit.name", target = "chargeUnitName")
  ServiceResponse toResponse(Service entity);
  List<ServiceResponse> toResponseList(List<Service> entities);
}
