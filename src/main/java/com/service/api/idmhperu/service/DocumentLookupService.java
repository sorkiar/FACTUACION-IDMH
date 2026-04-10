package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DniRecordResponse;
import com.service.api.idmhperu.dto.response.RucRecordResponse;

public interface DocumentLookupService {

  ApiResponse<DniRecordResponse> queryDni(String dni);

  ApiResponse<RucRecordResponse> queryRuc(String ruc);
}
