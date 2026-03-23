package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ExchangeRateResponse;
import java.time.LocalDate;

public interface ExchangeRateService {
  ApiResponse<ExchangeRateResponse> findByDate(LocalDate date);
}
