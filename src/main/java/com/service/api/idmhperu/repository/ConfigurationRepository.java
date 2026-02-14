package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Configuration;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository
    extends JpaRepository<Configuration, Long> {

  List<Configuration> findByConfigGroupAndDeletedAtIsNull(String configGroup);
}
