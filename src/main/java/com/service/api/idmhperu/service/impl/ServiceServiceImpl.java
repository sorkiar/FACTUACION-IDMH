package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.ChargeUnit;
import com.service.api.idmhperu.dto.entity.ServiceCategory;
import com.service.api.idmhperu.dto.filter.ServiceFilter;
import com.service.api.idmhperu.dto.mapper.ServiceMapper;
import com.service.api.idmhperu.dto.request.ServiceRequest;
import com.service.api.idmhperu.dto.request.ServiceStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ServiceResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.ChargeUnitRepository;
import com.service.api.idmhperu.repository.ServiceCategoryRepository;
import com.service.api.idmhperu.repository.ServiceRepository;
import com.service.api.idmhperu.repository.spec.ServiceSpecification;
import com.service.api.idmhperu.service.ServiceService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
  private final ServiceRepository repository;
  private final ServiceCategoryRepository serviceCategoryRepository;
  private final ChargeUnitRepository chargeUnitRepository;
  private final ServiceMapper mapper;

  @Override
  public ApiResponse<List<ServiceResponse>> findAll(ServiceFilter filter) {
    return new ApiResponse<>(
        "Servicios listados correctamente",
        mapper.toResponseList(
            repository.findAll(ServiceSpecification.byFilter(filter))
        )
    );
  }

  @Override
  @Transactional
  public ApiResponse<ServiceResponse> create(ServiceRequest request) {
    if (repository.existsBySku(request.getSku())) {
      throw new BusinessValidationException("El SKU ya existe");
    }

    ServiceCategory category = serviceCategoryRepository.findById(
        request.getServiceCategoryId()
    ).orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

    ChargeUnit chargeUnit = chargeUnitRepository.findById(
        request.getChargeUnitId()
    ).orElseThrow(() -> new ResourceNotFoundException("Unidad de cobro no encontrada"));

    com.service.api.idmhperu.dto.entity.Service service = new com.service.api.idmhperu.dto.entity.Service();
    service.setSku(request.getSku());
    service.setName(request.getName());
    service.setServiceCategory(category);
    service.setChargeUnit(chargeUnit);
    service.setPrice(request.getPrice());
    service.setEstimatedTime(request.getEstimatedTime());
    service.setExcludesDescription(request.getExcludesDescription());
    service.setIncludesDescription(request.getIncludesDescription());
    service.setExpectedDelivery(request.getExpectedDelivery());
    service.setConditions(request.getConditions());
    service.setRequiresMaterials(request.getRequiresMaterials());
    service.setRequiresPlan(request.getRequiresPlan());
    service.setShortDescription(request.getShortDescription());
    service.setDetailedDescription(request.getDetailedDescription());
    service.setStatus(1);
    service.setCreatedBy("system");

    return new ApiResponse<>(
        "Servicio registrado correctamente",
        mapper.toResponse(repository.save(service))
    );
  }

  @Override
  @Transactional
  public ApiResponse<ServiceResponse> update(Long id, ServiceRequest request) {
    com.service.api.idmhperu.dto.entity.Service service = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

    service.setName(request.getName());
    service.setPrice(request.getPrice());
    service.setEstimatedTime(request.getEstimatedTime());
    service.setExcludesDescription(request.getExcludesDescription());
    service.setIncludesDescription(request.getIncludesDescription());
    service.setIncludesDescription(request.getExcludesDescription());
    service.setConditions(request.getConditions());
    service.setRequiresMaterials(request.getRequiresMaterials());
    service.setRequiresPlan(request.getRequiresPlan());
    service.setShortDescription(request.getShortDescription());
    service.setDetailedDescription(request.getDetailedDescription());
    service.setUpdatedBy("system");

    return new ApiResponse<>(
        "Servicio actualizado correctamente",
        mapper.toResponse(repository.save(service))
    );
  }

  @Override
  @Transactional
  public ApiResponse<Void> updateStatus(Long id, ServiceStatusRequest request) {
    com.service.api.idmhperu.dto.entity.Service service = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

    service.setStatus(request.getStatus());
    service.setUpdatedBy("system");

    repository.save(service);

    return new ApiResponse<>("Estado actualizado correctamente", null);
  }
}
