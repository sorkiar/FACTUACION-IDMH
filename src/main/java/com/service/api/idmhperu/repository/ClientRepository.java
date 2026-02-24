package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Client;
import java.time.LocalDateTime;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientRepository
    extends JpaRepository<Client, Long>,
    JpaSpecificationExecutor<Client> {

  @Override
  @EntityGraph(attributePaths = {
      "documentType",
      "personType",
  })
  @NullMarked
  List<Client> findAll(Specification<Client> spec);

  long countByDeletedAtIsNull();

  long countByCreatedAtBetweenAndDeletedAtIsNull(LocalDateTime start, LocalDateTime end);
}
