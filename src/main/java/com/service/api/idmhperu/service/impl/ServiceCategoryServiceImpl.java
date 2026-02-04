package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.filter.ServiceCategoryFilter;
import com.service.api.idmhperu.dto.mapper.ServiceCategoryMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ServiceCategoryResponse;
import com.service.api.idmhperu.repository.ServiceCategoryRepository;
import com.service.api.idmhperu.repository.spec.ServiceCategorySpecification;
import com.service.api.idmhperu.service.ServiceCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceCategoryServiceImpl implements ServiceCategoryService {
  private final ServiceCategoryRepository repository;
  private final ServiceCategoryMapper mapper;

  @Override
  public ApiResponse<List<ServiceCategoryResponse>> findAll(ServiceCategoryFilter filter) {
    return new ApiResponse<>(
        "Categorías de servicio listadas correctamente",
        mapper.toResponseList(
            repository.findAll(ServiceCategorySpecification.byFilter(filter))
        )
    );
  }
}
