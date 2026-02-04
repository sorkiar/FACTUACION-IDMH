package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.UnitMeasure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UnitMeasureRepository
    extends JpaRepository<UnitMeasure, Long>,
    JpaSpecificationExecutor<UnitMeasure> {
}
