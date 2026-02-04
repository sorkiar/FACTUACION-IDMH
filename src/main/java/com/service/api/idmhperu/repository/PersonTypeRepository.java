package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.PersonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonTypeRepository
    extends JpaRepository<PersonType, Long>,
    JpaSpecificationExecutor<PersonType> {
}
