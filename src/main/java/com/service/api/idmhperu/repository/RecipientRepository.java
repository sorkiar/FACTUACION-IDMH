package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Recipient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RecipientRepository
    extends JpaRepository<Recipient, Long>,
    JpaSpecificationExecutor<Recipient> {

  Optional<Recipient> findByIdAndStatusNot(Long id, Integer status);
}
