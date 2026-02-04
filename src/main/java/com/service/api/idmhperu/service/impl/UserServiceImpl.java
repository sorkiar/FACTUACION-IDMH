package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.DocumentType;
import com.service.api.idmhperu.dto.entity.Profile;
import com.service.api.idmhperu.dto.entity.User;
import com.service.api.idmhperu.dto.filter.UserFilter;
import com.service.api.idmhperu.dto.mapper.UserMapper;
import com.service.api.idmhperu.dto.request.UserRequest;
import com.service.api.idmhperu.dto.request.UserStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.UserResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.DocumentTypeRepository;
import com.service.api.idmhperu.repository.ProfileRepository;
import com.service.api.idmhperu.repository.UserRepository;
import com.service.api.idmhperu.repository.spec.UserSpecification;
import com.service.api.idmhperu.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository repository;
  private final DocumentTypeRepository documentTypeRepository;
  private final ProfileRepository profileRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper mapper;

  @Override
  public ApiResponse<List<UserResponse>> findAll(UserFilter filter) {
    return new ApiResponse<>(
        "Usuarios listados correctamente",
        mapper.toResponseList(
            repository.findAll(UserSpecification.byFilter(filter))
        )
    );
  }

  @Override
  public ApiResponse<UserResponse> create(UserRequest request) {

    if (repository.existsByUsername(request.getUsername())) {
      throw new BusinessValidationException("El usuario ya existe");
    }

    DocumentType documentType = documentTypeRepository.findById(
        request.getDocumentTypeId()
    ).orElseThrow(() -> new ResourceNotFoundException("Tipo de documento no encontrado"));

    Profile profile = profileRepository.findById(
        request.getProfileId()
    ).orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado"));

    User user = new User();
    user.setDocumentType(documentType);
    user.setProfile(profile);
    user.setDocumentNumber(request.getDocumentNumber());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setStatus(1);
    user.setCreatedBy("system");

    return new ApiResponse<>(
        "Usuario registrado correctamente",
        mapper.toResponse(repository.save(user))
    );
  }

  @Override
  public ApiResponse<UserResponse> update(Long id, UserRequest request) {

    User user = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

    user.setDocumentNumber(request.getDocumentNumber());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setUpdatedBy("system");

    return new ApiResponse<>(
        "Usuario actualizado correctamente",
        mapper.toResponse(repository.save(user))
    );
  }

  @Override
  public ApiResponse<Void> updateStatus(Long id, UserStatusRequest request) {

    User user = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

    user.setStatus(request.getStatus());
    user.setUpdatedBy("system");

    repository.save(user);

    return new ApiResponse<>("Estado actualizado correctamente", null);
  }
}
