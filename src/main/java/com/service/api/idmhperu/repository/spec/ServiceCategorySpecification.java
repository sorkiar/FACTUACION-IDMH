package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.ServiceCategory;
import com.service.api.idmhperu.dto.filter.ServiceCategoryFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ServiceCategorySpecification {
  public static Specification<ServiceCategory> byFilter(ServiceCategoryFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.notEqual(root.get("status"), 2));

      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
