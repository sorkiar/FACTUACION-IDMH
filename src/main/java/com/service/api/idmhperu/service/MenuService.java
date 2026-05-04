package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.request.MenuRequest;
import com.service.api.idmhperu.dto.request.MenuStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.MenuResponse;
import com.service.api.idmhperu.dto.response.SidebarItemResponse;
import java.util.List;

public interface MenuService {

  ApiResponse<List<MenuResponse>> findAll();

  ApiResponse<MenuResponse> findById(Long id);

  ApiResponse<MenuResponse> create(MenuRequest request);

  ApiResponse<MenuResponse> update(Long id, MenuRequest request);

  ApiResponse<Void> updateStatus(Long id, MenuStatusRequest request);

  ApiResponse<List<SidebarItemResponse>> getSidebar();

  ApiResponse<List<SidebarItemResponse>> getNavbar();

  ApiResponse<List<SidebarItemResponse>> getInternal();

  ApiResponse<List<String>> getAllowedPaths();
}
