package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository
    extends JpaRepository<User, Long>,
    JpaSpecificationExecutor<User> {
  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);
}
