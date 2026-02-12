package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.DocumentTypeSunat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentTypeSunatRepository
    extends JpaRepository<DocumentTypeSunat, String>,
    JpaSpecificationExecutor<DocumentTypeSunat> {

  Optional<DocumentTypeSunat> findByCodeAndStatusNot(String code, Integer status);
}
