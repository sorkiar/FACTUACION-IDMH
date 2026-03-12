package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Configuration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository
    extends JpaRepository<Configuration, Long> {

  List<Configuration> findByConfigGroupAndDeletedAtIsNull(String configGroup);

  Optional<Configuration> findByConfigGroupAndConfigKeyAndDeletedAtIsNull(
      String configGroup, String configKey);
}
