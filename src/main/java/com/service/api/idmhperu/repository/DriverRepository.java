package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DriverRepository
    extends JpaRepository<Driver, Long>,
    JpaSpecificationExecutor<Driver> {
}
