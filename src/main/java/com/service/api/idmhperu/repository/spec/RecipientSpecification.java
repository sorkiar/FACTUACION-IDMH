package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.Recipient;
import com.service.api.idmhperu.dto.filter.RecipientFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class RecipientSpecification {

  public static Specification<Recipient> byFilter(RecipientFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.notEqual(root.get("status"), 2));

      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      if (filter.getDocNumber() != null && !filter.getDocNumber().isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("docNumber")),
            "%" + filter.getDocNumber().toLowerCase() + "%"));
      }

      if (filter.getName() != null && !filter.getName().isBlank()) {
        predicates.add(cb.like(cb.lower(root.get("name")),
            "%" + filter.getName().toLowerCase() + "%"));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
