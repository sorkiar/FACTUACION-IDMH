package com.service.api.idmhperu.repository;

import com.service.api.idmhperu.dto.entity.ExchangeRate;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

  boolean existsByDateAndType(LocalDate date, String type);

  List<ExchangeRate> findByDate(LocalDate date);

  List<ExchangeRate> findByDateBetweenAndType(LocalDate start, LocalDate end, String type);
}
