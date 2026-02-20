package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.CreditDebitNote;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CreditDebitNoteRepository
    extends JpaRepository<CreditDebitNote, Long>,
    JpaSpecificationExecutor<CreditDebitNote> {

  @Override
  @EntityGraph(attributePaths = {
      "sale",
      "sale.client",
      "sale.client.documentType",
      "sale.client.personType",
      "originalDocument",
      "originalDocument.documentTypeSunat",
      "documentTypeSunat",
      "documentSeries",
      "creditDebitNoteType",
      "items",
  })
  @NullMarked
  List<CreditDebitNote> findAll(Specification<CreditDebitNote> spec);

  @EntityGraph(attributePaths = {
      "sale",
      "sale.client",
      "sale.client.documentType",
      "sale.client.personType",
      "originalDocument",
      "originalDocument.documentTypeSunat",
      "documentTypeSunat",
      "documentSeries",
      "creditDebitNoteType",
      "items",
  })
  Optional<CreditDebitNote> findByIdAndDeletedAtIsNull(Long id);

  @EntityGraph(attributePaths = {
      "sale",
      "sale.client",
      "sale.client.documentType",
      "sale.client.personType",
      "originalDocument",
      "originalDocument.documentTypeSunat",
      "documentTypeSunat",
      "documentSeries",
      "creditDebitNoteType",
      "items",
  })
  List<CreditDebitNote> findByStatusAndDocumentTypeSunat_CodeAndDeletedAtIsNull(
      String status, String code);
}
