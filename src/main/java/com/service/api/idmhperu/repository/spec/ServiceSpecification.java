package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.Service;
import com.service.api.idmhperu.dto.filter.ServiceFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ServiceSpecification {
  public static Specification<Service> byFilter(ServiceFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.notEqual(root.get("status"), 2));

      if (filter.getId() != null) {
        predicates.add(cb.equal(root.get("id"), filter.getId()));
      }

      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      if (filter.getServiceCategoryId() != null) {
        predicates.add(cb.equal(
            root.get("serviceCategory").get("id"),
            filter.getServiceCategoryId()));
      }

      if (filter.getSku() != null) {
        predicates.add(cb.like(
            cb.lower(root.get("sku")),
            "%" + filter.getSku().toLowerCase() + "%"));
      }

      if (filter.getName() != null) {
        predicates.add(cb.like(
            cb.lower(root.get("name")),
            "%" + filter.getName().toLowerCase() + "%"));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
