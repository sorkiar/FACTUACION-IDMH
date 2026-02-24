package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DashboardResponse;
import com.service.api.idmhperu.dto.response.MonthlyRevenueResponse;

public interface DashboardService {

  ApiResponse<DashboardResponse> getDashboard();

  ApiResponse<MonthlyRevenueResponse> getMonthlyRevenue(int year, int month);
}
