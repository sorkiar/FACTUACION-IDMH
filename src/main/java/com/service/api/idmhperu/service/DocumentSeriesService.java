package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DocumentSeriesResponse;

public interface DocumentSeriesService {

  ApiResponse<DocumentSeriesResponse> getNextSequencePreview(String documentTypeCode);

  ApiResponse<DocumentSeriesResponse> getNextSequenceById(Long seriesId);

}
