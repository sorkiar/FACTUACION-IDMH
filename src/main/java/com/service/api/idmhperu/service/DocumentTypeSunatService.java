package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.DocumentTypeSunatFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DocumentTypeSunatResponse;
import java.util.List;

public interface DocumentTypeSunatService {
  ApiResponse<List<DocumentTypeSunatResponse>> findAll(DocumentTypeSunatFilter filter);
}
