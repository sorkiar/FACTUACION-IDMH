package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.ProfileFilter;
import com.service.api.idmhperu.dto.request.ProfileMenuRequest;
import com.service.api.idmhperu.dto.request.ProfileRequest;
import com.service.api.idmhperu.dto.request.ProfileStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.MenuResponse;
import com.service.api.idmhperu.dto.response.ProfileResponse;
import java.util.List;

public interface ProfileService {

  ApiResponse<List<ProfileResponse>> findAll(ProfileFilter filter);

  ApiResponse<ProfileResponse> findById(Long id);

  ApiResponse<ProfileResponse> create(ProfileRequest request);

  ApiResponse<ProfileResponse> update(Long id, ProfileRequest request);

  ApiResponse<Void> updateStatus(Long id, ProfileStatusRequest request);

  ApiResponse<List<MenuResponse>> getMenus(Long id);

  ApiResponse<Void> updateMenus(Long id, ProfileMenuRequest request);
}
