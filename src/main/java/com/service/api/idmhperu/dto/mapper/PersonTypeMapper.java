package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.PersonType;
import com.service.api.idmhperu.dto.response.PersonTypeResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonTypeMapper {
  PersonTypeResponse toResponse(PersonType entity);
  List<PersonTypeResponse> toResponseList(List<PersonType> entities);
}
