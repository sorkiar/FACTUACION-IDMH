package com.service.api.idmhperu.repository.spec;

import com.service.api.idmhperu.dto.entity.Profile;
import com.service.api.idmhperu.dto.filter.ProfileFilter;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public class ProfileSpecification {

  public static Specification<Profile> byFilter(ProfileFilter filter) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.notEqual(root.get("status"), 2));

      if (filter.getName() != null) {
        predicates.add(cb.like(
            cb.lower(root.get("name")),
            "%" + filter.getName().toLowerCase() + "%"));
      }
      if (filter.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), filter.getStatus()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
