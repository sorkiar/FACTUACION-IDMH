package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.ServiceCategoryFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ServiceCategoryResponse;
import java.util.List;

public interface ServiceCategoryService {
  ApiResponse<List<ServiceCategoryResponse>> findAll(ServiceCategoryFilter filter);
}
