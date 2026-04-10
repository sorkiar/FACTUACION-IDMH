package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CarrierRepository
    extends JpaRepository<Carrier, Long>,
    JpaSpecificationExecutor<Carrier> {
}
