package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.DocumentSeries;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocumentSeriesRepository
    extends JpaRepository<DocumentSeries, Long>,
    JpaSpecificationExecutor<DocumentSeries> {

  Optional<DocumentSeries> findByIdAndStatusNot(Long id, Integer status);

  Optional<DocumentSeries> findByDocumentTypeSunat_CodeAndSeriesAndStatusNot(
      String code, String series, Integer status);
}
