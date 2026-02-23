package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Ubigeo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UbigeoRepository
    extends JpaRepository<Ubigeo, String>,
    JpaSpecificationExecutor<Ubigeo> {
}
