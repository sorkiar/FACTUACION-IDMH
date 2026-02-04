package com.service.api.idmhperu.service;

import com.service.api.idmhperu.dto.filter.UserFilter;
import com.service.api.idmhperu.dto.request.UserRequest;
import com.service.api.idmhperu.dto.request.UserStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.UserResponse;
import java.util.List;

public interface UserService {
  ApiResponse<List<UserResponse>> findAll(UserFilter filter);

  ApiResponse<UserResponse> create(UserRequest request);

  ApiResponse<UserResponse> update(Long id, UserRequest request);

  ApiResponse<Void> updateStatus(Long id, UserStatusRequest request);
}
