package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.CreditDebitNote;
import com.service.api.idmhperu.dto.filter.CreditDebitNoteFilter;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class CreditDebitNoteSpecification {

  public static Specification<CreditDebitNote> byFilter(CreditDebitNoteFilter filter) {

    return (root, query, cb) -> {

      List<Predicate> predicates = new ArrayList<>();

      predicates.add(cb.isNull(root.get("deletedAt")));

      if (filter.getId() != null) {
        predicates.add(cb.equal(root.get("id"), filter.getId()));
      }

      if (filter.getSaleId() != null) {
        predicates.add(cb.equal(root.get("sale").get("id"), filter.getSaleId()));
      }

      if (filter.getDocumentTypeCode() != null) {
        predicates.add(cb.equal(
            root.get("documentTypeSunat").get("code"),
            filter.getDocumentTypeCode()));
      }

      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      if (filter.getStartDate() != null && filter.getEndDate() != null) {
        LocalDateTime start = filter.getStartDate().atStartOfDay();
        LocalDateTime end = filter.getEndDate().atTime(23, 59, 59);
        predicates.add(cb.between(root.get("issueDate"), start, end));
      }

      query.orderBy(cb.desc(root.get("issueDate")));
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
