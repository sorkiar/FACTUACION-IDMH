package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.request.ConfigurationRequest;
import com.service.api.idmhperu.dto.request.ConfigurationUpdateRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ConfigurationResponse;
import java.util.List;
import java.util.Map;

public interface ConfigurationService {

  Map<String, String> getGroup(String group);

  ApiResponse<List<ConfigurationResponse>> findEditable();

  ApiResponse<ConfigurationResponse> create(ConfigurationRequest request);

  ApiResponse<ConfigurationResponse> update(Long id, ConfigurationUpdateRequest request);
}
