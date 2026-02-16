package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.Document;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentRepository
    extends JpaRepository<Document, Long>,
    JpaSpecificationExecutor<Document> {

  @EntityGraph(attributePaths = {
      "sale",
      "sale.client",
      "sale.items",
      "documentTypeSunat",
      "documentSeries",
      "documentSeries.documentTypeSunat",
  })
  List<Document> findByStatusAndDocumentTypeSunat_CodeAndDeletedAtIsNull(String status, String code);

  @EntityGraph(attributePaths = {
      "sale",
      "sale.client",
      "sale.items",
      "documentTypeSunat",
      "documentSeries",
      "documentSeries.documentTypeSunat",
  })
  Optional<Document> findBySaleId(Long saleId);
}
