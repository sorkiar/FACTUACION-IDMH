package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.DetractionCodeFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DetractionCodeResponse;
import java.util.List;

public interface DetractionCodeService {
  ApiResponse<List<DetractionCodeResponse>> findAll(DetractionCodeFilter filter);
}
