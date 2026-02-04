package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.CategoryFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
  ApiResponse<List<CategoryResponse>> findAll(CategoryFilter filter);
}
