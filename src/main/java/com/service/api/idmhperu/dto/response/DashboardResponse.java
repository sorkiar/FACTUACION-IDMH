package com.service.api.idmhperu.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

  private Long totalClients;
  private Long newClientsToday;

  private Long totalSalesWeek;
  private Long newSalesToday;

  private BigDecimal revenueWeek;
  private BigDecimal revenueToday;
}
