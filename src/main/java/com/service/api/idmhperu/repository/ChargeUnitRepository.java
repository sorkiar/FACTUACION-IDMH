package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.ChargeUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChargeUnitRepository
    extends JpaRepository<ChargeUnit, Long>,
    JpaSpecificationExecutor<ChargeUnit> {
}
