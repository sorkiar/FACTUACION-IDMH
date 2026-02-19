package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.CreditDebitNoteType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CreditDebitNoteTypeRepository
    extends JpaRepository<CreditDebitNoteType, String>,
    JpaSpecificationExecutor<CreditDebitNoteType> {

  List<CreditDebitNoteType> findByNoteCategoryAndStatusNot(String noteCategory, Integer status);

  Optional<CreditDebitNoteType> findByCodeAndStatusNot(String code, Integer status);
}
