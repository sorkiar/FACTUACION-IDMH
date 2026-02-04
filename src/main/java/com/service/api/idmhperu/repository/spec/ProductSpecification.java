package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.Product;
import com.service.api.idmhperu.dto.filter.ProductFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
  public static Specification<Product> byFilter(ProductFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.notEqual(root.get("status"), 2));

      if (filter.getId() != null) {
        predicates.add(cb.equal(root.get("id"), filter.getId()));
      }

      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      if (filter.getCategoryId() != null) {
        predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
      }

      if (filter.getSku() != null) {
        predicates.add(cb.like(root.get("sku"), "%" + filter.getSku() + "%"));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
