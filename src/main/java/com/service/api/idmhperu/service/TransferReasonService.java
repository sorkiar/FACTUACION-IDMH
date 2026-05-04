package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.TransferReasonFilter;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.TransferReasonResponse;
import java.util.List;

public interface TransferReasonService {

  ApiResponse<List<TransferReasonResponse>> findAll(TransferReasonFilter filter);
}
