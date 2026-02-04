package com.service.api.idmhperu.dto.mapper;

import com.service.api.idmhperu.dto.entity.Category;
import com.service.api.idmhperu.dto.response.CategoryResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
  CategoryResponse toResponse(Category entity);
  List<CategoryResponse> toResponseList(List<Category> entities);
}
