package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.DocumentSeries;
import com.service.api.idmhperu.dto.mapper.DocumentSeriesMapper;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DocumentSeriesResponse;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.DocumentSeriesRepository;
import com.service.api.idmhperu.service.DocumentSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentSeriesServiceImpl implements DocumentSeriesService {

  private final DocumentSeriesRepository repository;
  private final DocumentSeriesMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public ApiResponse<DocumentSeriesResponse> getNextSequencePreview(
      String documentTypeCode
  ) {

    DocumentSeries series = repository
        .findFirstByDocumentTypeSunat_CodeAndStatusNotOrderByIdAsc(documentTypeCode, 2)
        .orElseThrow(() ->
            new ResourceNotFoundException(
                "No existe serie activa para el tipo de documento: " + documentTypeCode
            )
        );

    DocumentSeriesResponse response = mapper.toResponse(series);

    response.setSequence(series.getCurrentSequence() + 1);

    return new ApiResponse<>(
        "Correlativo obtenido correctamente",
        response
    );
  }

  @Override
  @Transactional(readOnly = true)
  public ApiResponse<DocumentSeriesResponse> getNextSequenceById(Long seriesId) {
    DocumentSeries series = repository.findById(seriesId)
        .orElseThrow(() ->
            new ResourceNotFoundException("No existe serie con id: " + seriesId)
        );

    DocumentSeriesResponse response = mapper.toResponse(series);
    response.setSequence(series.getCurrentSequence() + 1);

    return new ApiResponse<>("Correlativo obtenido correctamente", response);
  }
}
