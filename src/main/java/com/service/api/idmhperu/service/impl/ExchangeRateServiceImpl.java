package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.ExchangeRate;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ExchangeRateResponse;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.ExchangeRateRepository;
import com.service.api.idmhperu.service.ExchangeRateService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

  private final ExchangeRateRepository exchangeRateRepository;

  @Override
  public ApiResponse<ExchangeRateResponse> findByDate(LocalDate date) {
    List<ExchangeRate> rates = exchangeRateRepository.findByDate(date);

    if (rates.isEmpty()) {
      throw new ResourceNotFoundException(
          "No se encontró tipo de cambio para la fecha: " + date);
    }

    ExchangeRateResponse response = new ExchangeRateResponse();
    response.setDate(date);

    for (ExchangeRate rate : rates) {
      if ("C".equals(rate.getType())) {
        response.setPurchase(rate.getValue());
      } else if ("V".equals(rate.getType())) {
        response.setSale(rate.getValue());
      }
    }

    return new ApiResponse<>("Tipo de cambio obtenido correctamente", response);
  }
}
