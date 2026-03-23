package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.DetractionCode;
import com.service.api.idmhperu.dto.filter.DetractionCodeFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class DetractionCodeSpecification {

  public static Specification<DetractionCode> byFilter(DetractionCodeFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (filter.getCode() != null && !filter.getCode().isBlank()) {
        predicates.add(cb.like(root.get("code"), "%" + filter.getCode().trim() + "%"));
      }

      if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
        predicates.add(cb.equal(root.get("category"), filter.getCategory().trim().toUpperCase()));
      }

      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
