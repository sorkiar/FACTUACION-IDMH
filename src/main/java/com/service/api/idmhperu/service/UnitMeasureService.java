package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.UnitMeasureFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.UnitMeasureResponse;
import java.util.List;

public interface UnitMeasureService {
  ApiResponse<List<UnitMeasureResponse>> findAll(UnitMeasureFilter filter);
}
