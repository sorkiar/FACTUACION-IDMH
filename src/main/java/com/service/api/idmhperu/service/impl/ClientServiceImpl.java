package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Client;
import com.service.api.idmhperu.dto.entity.PersonType;
import com.service.api.idmhperu.dto.filter.ClientFilter;
import com.service.api.idmhperu.dto.mapper.ClientMapper;
import com.service.api.idmhperu.dto.request.ClientRequest;
import com.service.api.idmhperu.dto.request.ClientStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ClientResponse;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.ClientRepository;
import com.service.api.idmhperu.repository.DocumentTypeRepository;
import com.service.api.idmhperu.repository.PersonTypeRepository;
import com.service.api.idmhperu.repository.spec.ClientSpecification;
import com.service.api.idmhperu.service.ClientService;
import com.service.api.idmhperu.util.ClientValidator;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
  private final ClientRepository repository;
  private final PersonTypeRepository personTypeRepository;
  private final DocumentTypeRepository documentTypeRepository;
  private final ClientValidator validator;
  private final ClientMapper mapper;

  @Override
  public ApiResponse<List<ClientResponse>> findAll(ClientFilter filter) {
    return new ApiResponse<>("Clientes listados correctamente",
        mapper.toResponseList(repository.findAll(ClientSpecification.byFilter(filter))));
  }

  @Override
  @Transactional
  public ApiResponse<ClientResponse> create(ClientRequest request) {
    PersonType personType = personTypeRepository.findById(request.getPersonTypeId())
        .orElseThrow(() -> new ResourceNotFoundException("Tipo de persona no válido"));

    validator.validateByPersonType(personType, request);

    Client client = mapper.toEntity(request);
    client.setPersonType(personType);
    client.setDocumentType(documentTypeRepository.findById(request.getDocumentTypeId())
        .orElseThrow(() -> new ResourceNotFoundException("Tipo de documento no válido")));
    client.setStatus(1);

    return new ApiResponse<>("Cliente registrado correctamente",
        mapper.toResponse(repository.save(client)));
  }

  @Override
  @Transactional
  public ApiResponse<ClientResponse> update(Long id, ClientRequest request) {

    Client client = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

    PersonType personType = personTypeRepository.findById(request.getPersonTypeId())
        .orElseThrow(() -> new ResourceNotFoundException("Tipo de persona no válido"));

    validator.validateByPersonType(personType, request);

    mapper.updateEntity(client, request);
    client.setPersonType(personType);
    client.setDocumentType(documentTypeRepository.findById(request.getDocumentTypeId())
        .orElseThrow(() -> new ResourceNotFoundException("Tipo de documento no válido")));

    return new ApiResponse<>("Cliente actualizado correctamente",
        mapper.toResponse(repository.save(client)));
  }

  @Override
  @Transactional
  public ApiResponse<Void> updateStatus(Long id, ClientStatusRequest request) {

    Client client = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

    client.setStatus(request.getStatus());

    return new ApiResponse<>("Estado del cliente actualizado correctamente", null);
  }
}
