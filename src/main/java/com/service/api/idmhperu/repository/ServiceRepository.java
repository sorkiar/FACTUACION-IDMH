package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Service;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ServiceRepository
    extends JpaRepository<Service, Long>,
    JpaSpecificationExecutor<Service> {

  @Override
  @EntityGraph(attributePaths = {
      "serviceCategory",
      "chargeUnit",
  })
  @NullMarked
  List<Service> findAll(Specification<Service> spec);

  boolean existsBySku(String sku);

  @EntityGraph(attributePaths = {
      "serviceCategory",
      "chargeUnit",
  })
  Optional<Service> findByIdAndStatusNot(Long id, Integer status);
}
