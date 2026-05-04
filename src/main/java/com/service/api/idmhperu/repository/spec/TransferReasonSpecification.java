package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.TransferReason;
import com.service.api.idmhperu.dto.filter.TransferReasonFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class TransferReasonSpecification {

  public static Specification<TransferReason> byFilter(TransferReasonFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (filter.getId() != null) {
        predicates.add(cb.equal(root.get("id"), filter.getId()));
      }

      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      query.orderBy(cb.asc(root.get("code")));
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
