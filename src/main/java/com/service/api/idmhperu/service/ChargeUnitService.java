package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.ChargeUnitFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ChargeUnitResponse;
import java.util.List;

public interface ChargeUnitService {

  ApiResponse<List<ChargeUnitResponse>> findAll(ChargeUnitFilter filter);
}
