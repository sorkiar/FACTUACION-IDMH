package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.ServiceCategory;
import com.service.api.idmhperu.dto.response.ServiceCategoryResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceCategoryMapper {
  ServiceCategoryResponse toResponse(ServiceCategory entity);
  List<ServiceCategoryResponse> toResponseList(List<ServiceCategory> entities);
}
