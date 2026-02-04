package com.service.api.idmhperu.controller;

import com.service.api.idmhperu.dto.request.AuthRequest;
import com.service.api.idmhperu.dto.request.UserRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.UserAuthResponse;
import com.service.api.idmhperu.dto.response.UserResponse;
import com.service.api.idmhperu.security.AuthService;
import com.service.api.idmhperu.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
  private final AuthService service;
  private final UserService userService;

  @PostMapping("/login")
  public ApiResponse<UserAuthResponse> login(
      @Valid @RequestBody AuthRequest request) {
    return service.login(request);
  }

  @PostMapping("/register")
  public ApiResponse<UserResponse> create(
      @Valid @RequestBody UserRequest request
  ) {
    return userService.create(request);
  }
}
