package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Sale;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SaleRepository
    extends JpaRepository<Sale, Long>,
    JpaSpecificationExecutor<Sale> {

  @Override
  @EntityGraph(attributePaths = {
      "client",
      "items",
      "items.product",
      "items.service",
      "items.unitMeasure"
  })
  @NullMarked
  List<Sale> findAll(Specification<Sale> spec);

  @EntityGraph(attributePaths = {
      "client",
      "items",
      "items.product",
      "items.service",
      "items.unitMeasure"
  })
  Optional<Sale> findByIdAndDeletedAtIsNull(Long id);
}
