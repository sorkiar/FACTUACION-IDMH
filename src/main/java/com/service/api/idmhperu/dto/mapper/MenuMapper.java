package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.Menu;
import com.service.api.idmhperu.dto.response.MenuResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuMapper {

  @Mapping(source = "parent.id", target = "parentId")
  @Mapping(source = "status", target = "status")
  MenuResponse toResponse(Menu entity);

  List<MenuResponse> toResponseList(List<Menu> entities);
}
