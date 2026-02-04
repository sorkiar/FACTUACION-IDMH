package com.service.api.idmhperu.security;

import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    com.service.api.idmhperu.dto.entity.User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado o credenciales inválidas"));

    return new User(
        user.getUsername(),
        user.getPassword(),
        List.of(new SimpleGrantedAuthority("ROLE_" + user.getProfile().getCode()))
    );
  }
}
