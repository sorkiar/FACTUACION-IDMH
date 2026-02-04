package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.DocumentTypeFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DocumentTypeResponse;
import java.util.List;

public interface DocumentTypeService {
  ApiResponse<List<DocumentTypeResponse>> findAll(DocumentTypeFilter filter);
}
