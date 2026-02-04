package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceCategoryRepository
    extends JpaRepository<ServiceCategory, Long>,
    JpaSpecificationExecutor<ServiceCategory> {
}
