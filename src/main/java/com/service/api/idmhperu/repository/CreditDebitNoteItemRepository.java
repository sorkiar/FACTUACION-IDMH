package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.CreditDebitNoteItem;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditDebitNoteItemRepository extends JpaRepository<CreditDebitNoteItem, Long> {

  @EntityGraph(attributePaths = {"product", "service", "unitMeasure"})
  List<CreditDebitNoteItem> findByCreditDebitNoteIdAndDeletedAtIsNull(Long noteId);
}
