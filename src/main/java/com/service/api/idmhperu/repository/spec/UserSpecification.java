package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.User;
import com.service.api.idmhperu.dto.filter.UserFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
  public static Specification<User> byFilter(UserFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.notEqual(root.get("status"), 2));

      if (filter.getId() != null) {
        predicates.add(cb.equal(root.get("id"), filter.getId()));
      }

      if (filter.getUsername() != null) {
        predicates.add(cb.like(
            cb.lower(root.get("username")),
            "%" + filter.getUsername().toLowerCase() + "%"));
      }

      if (filter.getDocumentTypeId() != null) {
        predicates.add(cb.equal(
            root.get("documentType").get("id"),
            filter.getDocumentTypeId()));
      }

      if (filter.getDocumentNumber() != null) {
        predicates.add(cb.equal(
            root.get("documentNumber"),
            filter.getDocumentNumber()));
      }

      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
