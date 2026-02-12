package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Document;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentRepository
    extends JpaRepository<Document, Long>,
    JpaSpecificationExecutor<Document> {

  Optional<Document> findByIdAndDeletedAtIsNull(Long id);
}
