package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Carrier;
import com.service.api.idmhperu.dto.filter.CarrierFilter;
import com.service.api.idmhperu.dto.mapper.CarrierMapper;
import com.service.api.idmhperu.dto.request.CarrierRequest;
import com.service.api.idmhperu.dto.request.CarrierStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.CarrierResponse;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.CarrierRepository;
import com.service.api.idmhperu.repository.spec.CarrierSpecification;
import com.service.api.idmhperu.service.CarrierService;
import com.service.api.idmhperu.util.JwtUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarrierServiceImpl implements CarrierService {

  private final CarrierRepository repository;
  private final CarrierMapper mapper;

  @Override
  public ApiResponse<List<CarrierResponse>> findAll(CarrierFilter filter) {
    List<Carrier> carriers = repository.findAll(CarrierSpecification.byFilter(filter));
    return new ApiResponse<>("Transportistas listados correctamente", mapper.toResponseList(carriers));
  }

  @Override
  public ApiResponse<CarrierResponse> findById(Long id) {
    Carrier carrier = repository.findById(id)
        .filter(c -> !Integer.valueOf(2).equals(c.getStatus()))
        .orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado"));
    return new ApiResponse<>("Transportista obtenido correctamente", mapper.toResponse(carrier));
  }

  @Override
  public ApiResponse<CarrierResponse> create(CarrierRequest request) {
    String username = JwtUtils.extractUsernameFromContext();

    Carrier carrier = new Carrier();
    carrier.setDocType(request.getDocType() != null ? request.getDocType() : "RUC");
    carrier.setDocNumber(request.getDocNumber());
    carrier.setBusinessName(request.getBusinessName());
    carrier.setStatus(1);
    carrier.setCreatedBy(username);

    return new ApiResponse<>("Transportista registrado correctamente",
        mapper.toResponse(repository.save(carrier)));
  }

  @Override
  public ApiResponse<CarrierResponse> update(Long id, CarrierRequest request) {
    String username = JwtUtils.extractUsernameFromContext();

    Carrier carrier = repository.findById(id)
        .filter(c -> !Integer.valueOf(2).equals(c.getStatus()))
        .orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado"));

    carrier.setDocType(request.getDocType() != null ? request.getDocType() : "RUC");
    carrier.setDocNumber(request.getDocNumber());
    carrier.setBusinessName(request.getBusinessName());
    carrier.setUpdatedBy(username);

    return new ApiResponse<>("Transportista actualizado correctamente",
        mapper.toResponse(repository.save(carrier)));
  }

  @Override
  public ApiResponse<Void> updateStatus(Long id, CarrierStatusRequest request) {
    String username = JwtUtils.extractUsernameFromContext();

    Carrier carrier = repository.findById(id)
        .filter(c -> !Integer.valueOf(2).equals(c.getStatus()))
        .orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado"));

    carrier.setStatus(request.getStatus());
    carrier.setUpdatedBy(username);
    repository.save(carrier);

    return new ApiResponse<>("Estado actualizado correctamente", null);
  }
}
