package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Configuration;
import com.service.api.idmhperu.repository.ConfigurationRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigurationServiceImpl {
  private final ConfigurationRepository repository;

  public Map<String, String> getGroup(String group) {

    List<Configuration> configs =
        repository.findByConfigGroupAndDeletedAtIsNull(group);

    Map<String, String> map = new HashMap<>();

    for (Configuration config : configs) {
      map.put(config.getConfigKey(), config.getConfigValue());
    }

    return map;
  }
}
