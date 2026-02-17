package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.DocumentSeries;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentSeriesRepository
    extends JpaRepository<DocumentSeries, Long>,
    JpaSpecificationExecutor<DocumentSeries> {

  Optional<DocumentSeries> findByIdAndStatusNot(Long id, Integer status);

  Optional<DocumentSeries> findByDocumentTypeSunat_CodeAndSeriesAndStatusNot(
      String code, String series, Integer status);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT ds FROM DocumentSeries ds WHERE ds.id = :id")
  Optional<DocumentSeries> findByIdForUpdate(@Param("id") Long id);

  Optional<DocumentSeries> findFirstByDocumentTypeSunat_CodeAndStatusNotOrderByIdAsc(
      String code,
      Integer status
  );
}
