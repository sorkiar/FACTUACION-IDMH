package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Recipient;
import com.service.api.idmhperu.dto.filter.RecipientFilter;
import com.service.api.idmhperu.dto.mapper.RecipientMapper;
import com.service.api.idmhperu.dto.request.RecipientRequest;
import com.service.api.idmhperu.dto.request.RecipientStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.RecipientResponse;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.RecipientRepository;
import com.service.api.idmhperu.repository.spec.RecipientSpecification;
import com.service.api.idmhperu.service.RecipientService;
import com.service.api.idmhperu.util.JwtUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipientServiceImpl implements RecipientService {

  private final RecipientRepository repository;
  private final RecipientMapper mapper;

  @Override
  public ApiResponse<List<RecipientResponse>> findAll(RecipientFilter filter) {
    List<Recipient> entities = repository.findAll(RecipientSpecification.byFilter(filter));
    return new ApiResponse<>("Destinatarios listados correctamente",
        mapper.toResponseList(entities));
  }

  @Override
  public ApiResponse<RecipientResponse> findById(Long id) {
    Recipient entity = repository.findByIdAndStatusNot(id, 2)
        .orElseThrow(() -> new ResourceNotFoundException("Destinatario no encontrado"));
    return new ApiResponse<>("Destinatario obtenido correctamente", mapper.toResponse(entity));
  }

  @Override
  public ApiResponse<RecipientResponse> create(RecipientRequest request) {
    String username = JwtUtils.extractUsernameFromContext();
    Recipient entity = new Recipient();
    entity.setDocType(request.getDocType());
    entity.setDocNumber(request.getDocNumber());
    entity.setName(request.getName());
    entity.setAddress(request.getAddress());
    entity.setStatus(1);
    entity.setCreatedBy(username);
    entity = repository.save(entity);
    return new ApiResponse<>("Destinatario creado correctamente", mapper.toResponse(entity));
  }

  @Override
  public ApiResponse<RecipientResponse> update(Long id, RecipientRequest request) {
    String username = JwtUtils.extractUsernameFromContext();
    Recipient entity = repository.findByIdAndStatusNot(id, 2)
        .orElseThrow(() -> new ResourceNotFoundException("Destinatario no encontrado"));
    entity.setDocType(request.getDocType());
    entity.setDocNumber(request.getDocNumber());
    entity.setName(request.getName());
    entity.setAddress(request.getAddress());
    entity.setUpdatedBy(username);
    entity = repository.save(entity);
    return new ApiResponse<>("Destinatario actualizado correctamente", mapper.toResponse(entity));
  }

  @Override
  public ApiResponse<Void> updateStatus(Long id, RecipientStatusRequest request) {
    Recipient entity = repository.findByIdAndStatusNot(id, 2)
        .orElseThrow(() -> new ResourceNotFoundException("Destinatario no encontrado"));
    entity.setStatus(request.getStatus());
    entity.setUpdatedBy(JwtUtils.extractUsernameFromContext());
    repository.save(entity);
    return new ApiResponse<>("Estado del destinatario actualizado", null);
  }
}
