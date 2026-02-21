package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.RemissionGuide;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RemissionGuideRepository
    extends JpaRepository<RemissionGuide, Long>,
    JpaSpecificationExecutor<RemissionGuide> {

  @Override
  @EntityGraph(attributePaths = {
      "documentSeries", "documentSeries.documentTypeSunat",
      "items", "items.product",
      "drivers",
  })
  @NullMarked
  List<RemissionGuide> findAll(Specification<RemissionGuide> spec);

  @EntityGraph(attributePaths = {
      "documentSeries", "documentSeries.documentTypeSunat",
      "items", "items.product",
      "drivers",
  })
  Optional<RemissionGuide> findByIdAndDeletedAtIsNull(Long id);

  List<RemissionGuide> findByStatusAndDeletedAtIsNull(String status);
}
