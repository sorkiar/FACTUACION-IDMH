package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.DocumentTypeSunat;
import com.service.api.idmhperu.dto.filter.DocumentTypeSunatFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class DocumentTypeSunatSpecification {
  public static Specification<DocumentTypeSunat> byFilter(DocumentTypeSunatFilter filter) {

    return (root, query, cb) -> {

      List<Predicate> predicates = new ArrayList<>();

      predicates.add(cb.notEqual(root.get("status"), 2));

      if (filter.getCode() != null) {
        predicates.add(cb.equal(root.get("code"), filter.getCode()));
      }

      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
