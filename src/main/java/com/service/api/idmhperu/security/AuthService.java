package com.service.api.idmhperu.security;

import com.service.api.idmhperu.dto.request.AuthRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.UserAuthResponse;
import com.service.api.idmhperu.exception.LoginException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.UserRepository;
import com.service.api.idmhperu.util.JwtUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;

  public ApiResponse<UserAuthResponse> login(AuthRequest request) {
    com.service.api.idmhperu.dto.entity.User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado o credenciales inválidas"));

    if (user.getStatus() == 0) {
      throw new LoginException("Usuario inactivo, contacte con el administrador", 403);
    }

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new LoginException("Contraseña incorrecta", 401);
    }

    UserDetails userDetails = new User(
        user.getUsername(),
        user.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getProfile().getCode()))
    );

    UserAuthResponse response = new UserAuthResponse();
    response.setId(user.getId());
    response.setDocumentType(user.getDocumentType().getName());
    response.setDocumentNumber(user.getDocumentNumber());
    response.setProfile(user.getProfile().getName());
    response.setFirstName(user.getFirstName());
    response.setLastName(user.getLastName());
    response.setUsername(user.getUsername());
    response.setToken(jwtUtils.generateToken(userDetails));
    response.setStatus(user.getStatus());

    return new ApiResponse<>(
        "Inicio de sesión exitoso",
        response
    );
  }
}