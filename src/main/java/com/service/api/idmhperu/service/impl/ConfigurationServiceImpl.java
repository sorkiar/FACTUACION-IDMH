package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Configuration;
import com.service.api.idmhperu.dto.request.ConfigurationRequest;
import com.service.api.idmhperu.dto.request.ConfigurationUpdateRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ConfigurationResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.ConfigurationRepository;
import com.service.api.idmhperu.service.ConfigurationService;
import com.service.api.idmhperu.util.JwtUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfigurationServiceImpl implements ConfigurationService {

  private final ConfigurationRepository repository;

  @Override
  public Map<String, String> getGroup(String group) {
    List<Configuration> configs = repository.findByConfigGroupAndDeletedAtIsNull(group);
    Map<String, String> map = new HashMap<>();
    for (Configuration config : configs) {
      map.put(config.getConfigKey(), config.getConfigValue());
    }
    return map;
  }

  @Override
  public ApiResponse<List<ConfigurationResponse>> findEditable() {
    List<Configuration> configs = repository.findEditableOrdered(1);
    return new ApiResponse<>("Configuraciones obtenidas correctamente",
        configs.stream().map(this::toResponse).toList());
  }

  @Override
  @Transactional
  public ApiResponse<ConfigurationResponse> create(ConfigurationRequest request) {
    String username = JwtUtils.extractUsernameFromContext();

    if (repository.findByConfigGroupAndConfigKeyAndDeletedAtIsNull(
        request.getConfigGroup(), request.getConfigKey()).isPresent()) {
      throw new BusinessValidationException(
          "Ya existe una configuración con el grupo '" + request.getConfigGroup()
              + "' y clave '" + request.getConfigKey() + "'");
    }

    Configuration config = new Configuration();
    config.setConfigGroup(request.getConfigGroup());
    config.setConfigKey(request.getConfigKey());
    config.setConfigValue(request.getConfigValue());
    config.setConfigDatatype(request.getConfigDatatype());
    config.setDescription(request.getDescription());
    config.setEditable(request.getEditable());
    config.setCreatedBy(username);

    return new ApiResponse<>("Configuración creada correctamente",
        toResponse(repository.save(config)));
  }

  @Override
  @Transactional
  public ApiResponse<ConfigurationResponse> update(Long id, ConfigurationUpdateRequest request) {
    String username = JwtUtils.extractUsernameFromContext();

    Configuration config = repository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada"));

    config.setConfigValue(request.getConfigValue());
    config.setDescription(request.getDescription());
    config.setEditable(request.getEditable());
    config.setUpdatedBy(username);

    return new ApiResponse<>("Configuración actualizada correctamente",
        toResponse(repository.save(config)));
  }

  private ConfigurationResponse toResponse(Configuration config) {
    ConfigurationResponse response = new ConfigurationResponse();
    response.setId(config.getId());
    response.setConfigGroup(config.getConfigGroup());
    response.setConfigKey(config.getConfigKey());
    response.setConfigValue(config.getConfigValue());
    response.setConfigDatatype(config.getConfigDatatype());
    response.setDescription(config.getDescription());
    response.setEditable(config.getEditable());
    response.setSortOrder(config.getSortOrder());
    response.setColSpan(config.getColSpan());
    response.setCreatedAt(config.getCreatedAt());
    response.setUpdatedAt(config.getUpdatedAt());
    return response;
  }
}
