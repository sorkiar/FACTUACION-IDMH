package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.response.SalesReportResponse;
import java.time.LocalDate;

public interface ReportService {
  SalesReportResponse salesReport(LocalDate startDate, LocalDate endDate, String clientIds, String productIds);
}
