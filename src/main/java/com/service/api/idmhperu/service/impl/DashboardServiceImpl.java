package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DashboardResponse;
import com.service.api.idmhperu.dto.response.MonthlyRevenueResponse;
import com.service.api.idmhperu.repository.ClientRepository;
import com.service.api.idmhperu.repository.CreditDebitNoteRepository;
import com.service.api.idmhperu.repository.SaleRepository;
import com.service.api.idmhperu.service.DashboardService;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

  private final ClientRepository clientRepository;
  private final SaleRepository saleRepository;
  private final CreditDebitNoteRepository creditDebitNoteRepository;

  @Override
  public ApiResponse<DashboardResponse> getDashboard() {
    LocalDate today = LocalDate.now();
    LocalDateTime todayStart = today.atStartOfDay();
    LocalDateTime todayEnd = today.atTime(LocalTime.MAX);

    LocalDateTime weekStart = today.with(DayOfWeek.MONDAY).atStartOfDay();
    LocalDateTime weekEnd = today.with(DayOfWeek.SUNDAY).atTime(LocalTime.MAX);

    // Clientes
    long totalClients = clientRepository.countByDeletedAtIsNull();
    long newClientsToday = clientRepository.countByCreatedAtBetweenAndDeletedAtIsNull(todayStart, todayEnd);

    // Ventas
    long totalSalesWeek = saleRepository.countBySaleDateBetweenAndDeletedAtIsNull(weekStart, weekEnd);
    long newSalesToday = saleRepository.countBySaleDateBetweenAndDeletedAtIsNull(todayStart, todayEnd);

    // Ingresos semana: ventas - notas de crédito + notas de débito (solo ACEPTADO)
    BigDecimal salesWeek = saleRepository.sumTotalAmountBySaleDateBetween(weekStart, weekEnd);
    BigDecimal creditWeek = creditDebitNoteRepository.sumTotalAmountByIssueDateBetweenAndNoteCategory(weekStart, weekEnd, "CREDITO");
    BigDecimal debitWeek = creditDebitNoteRepository.sumTotalAmountByIssueDateBetweenAndNoteCategory(weekStart, weekEnd, "DEBITO");
    BigDecimal revenueWeek = salesWeek.subtract(creditWeek).add(debitWeek);

    // Ingresos hoy
    BigDecimal salesToday = saleRepository.sumTotalAmountBySaleDateBetween(todayStart, todayEnd);
    BigDecimal creditToday = creditDebitNoteRepository.sumTotalAmountByIssueDateBetweenAndNoteCategory(todayStart, todayEnd, "CREDITO");
    BigDecimal debitToday = creditDebitNoteRepository.sumTotalAmountByIssueDateBetweenAndNoteCategory(todayStart, todayEnd, "DEBITO");
    BigDecimal revenueToday = salesToday.subtract(creditToday).add(debitToday);

    DashboardResponse data = DashboardResponse.builder()
        .totalClients(totalClients)
        .newClientsToday(newClientsToday)
        .totalSalesWeek(totalSalesWeek)
        .newSalesToday(newSalesToday)
        .revenueWeek(revenueWeek)
        .revenueToday(revenueToday)
        .build();

    return new ApiResponse<>("Dashboard obtenido correctamente", data);
  }

  @Override
  public ApiResponse<MonthlyRevenueResponse> getMonthlyRevenue(int year, int month) {
    YearMonth yearMonth = YearMonth.of(year, month);
    LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
    LocalDateTime end = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
    int daysInMonth = yearMonth.lengthOfMonth();

    // Mapa día -> ingreso neto (inicializado en 0)
    Map<Integer, BigDecimal> revenueByDay = new HashMap<>();
    for (int d = 1; d <= daysInMonth; d++) {
      revenueByDay.put(d, BigDecimal.ZERO);
    }

    // Sumar ventas por día
    for (Object[] row : saleRepository.sumTotalAmountGroupedByDay(start, end)) {
      int day = ((Number) row[0]).intValue();
      BigDecimal amount = (BigDecimal) row[1];
      revenueByDay.merge(day, amount, BigDecimal::add);
    }

    // Restar notas de crédito por día (disminuyen el ingreso)
    for (Object[] row : creditDebitNoteRepository.sumTotalAmountGroupedByDayAndNoteCategory(start, end, "CREDITO")) {
      int day = ((Number) row[0]).intValue();
      BigDecimal amount = (BigDecimal) row[1];
      revenueByDay.merge(day, amount.negate(), BigDecimal::add);
    }

    // Sumar notas de débito por día (aumentan el ingreso)
    for (Object[] row : creditDebitNoteRepository.sumTotalAmountGroupedByDayAndNoteCategory(start, end, "DEBITO")) {
      int day = ((Number) row[0]).intValue();
      BigDecimal amount = (BigDecimal) row[1];
      revenueByDay.merge(day, amount, BigDecimal::add);
    }

    List<Integer> categories = new ArrayList<>();
    List<BigDecimal> series = new ArrayList<>();
    for (int d = 1; d <= daysInMonth; d++) {
      categories.add(d);
      series.add(revenueByDay.get(d));
    }

    return new ApiResponse<>("Ingresos mensuales obtenidos correctamente",
        MonthlyRevenueResponse.builder()
            .year(year)
            .month(month)
            .categories(categories)
            .series(series)
            .build());
  }
}
