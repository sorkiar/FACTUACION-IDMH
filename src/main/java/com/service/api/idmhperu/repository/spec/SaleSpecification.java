package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.Sale;
import com.service.api.idmhperu.dto.filter.SaleFilter;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class SaleSpecification {
  public static Specification<Sale> byFilter(SaleFilter filter) {

    return (root, query, cb) -> {

      List<Predicate> predicates = new ArrayList<>();

      predicates.add(cb.isNull(root.get("deletedAt")));

      if (filter.getId() != null) {
        predicates.add(cb.equal(root.get("id"), filter.getId()));
      }

      if (filter.getClientId() != null) {
        predicates.add(cb.equal(
            root.get("client").get("id"),
            filter.getClientId()));
      }

      if (filter.getSaleStatus() != null) {
        predicates.add(cb.equal(
            root.get("saleStatus"),
            filter.getSaleStatus()));
      }

      if (filter.getPaymentStatus() != null) {
        predicates.add(cb.equal(
            root.get("paymentStatus"),
            filter.getPaymentStatus()));
      }

      if (filter.getStartDate() != null && filter.getEndDate() != null) {
        LocalDateTime start = filter.getStartDate().atStartOfDay();
        LocalDateTime end = filter.getEndDate().atTime(23, 59, 59);

        predicates.add(cb.between(
            root.get("saleDate"),
            start,
            end));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
