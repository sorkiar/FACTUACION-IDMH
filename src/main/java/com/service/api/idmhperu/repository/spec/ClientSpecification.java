package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.Client;
import com.service.api.idmhperu.dto.filter.ClientFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ClientSpecification {
  public static Specification<Client> byFilter(ClientFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.notEqual(root.get("status"), 2));

      if (filter.getId() != null) {
        predicates.add(cb.equal(root.get("id"), filter.getId()));
      }

      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      if (filter.getDocumentTypeId() != null) {
        predicates.add(cb.equal(root.get("documentType").get("id"), filter.getDocumentTypeId()));
      }

      if (filter.getDocumentNumber() != null) {
        predicates.add(cb.like(root.get("documentNumber"), "%" + filter.getDocumentNumber() + "%"));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
