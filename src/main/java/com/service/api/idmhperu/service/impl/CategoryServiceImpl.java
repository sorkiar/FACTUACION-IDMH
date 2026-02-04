package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.filter.CategoryFilter;
import com.service.api.idmhperu.dto.mapper.CategoryMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.CategoryResponse;
import com.service.api.idmhperu.repository.CategoryRepository;
import com.service.api.idmhperu.repository.spec.CategorySpecification;
import com.service.api.idmhperu.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository repository;
  private final CategoryMapper mapper;

  @Override
  public ApiResponse<List<CategoryResponse>> findAll(CategoryFilter filter) {
    return new ApiResponse<>(
        "Categorías listadas correctamente",
        mapper.toResponseList(
            repository.findAll(CategorySpecification.byFilter(filter))
        )
    );
  }
}
