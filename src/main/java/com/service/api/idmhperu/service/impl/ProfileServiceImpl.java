package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Menu;
import com.service.api.idmhperu.dto.entity.Profile;
import com.service.api.idmhperu.dto.filter.ProfileFilter;
import com.service.api.idmhperu.dto.mapper.MenuMapper;
import com.service.api.idmhperu.dto.mapper.ProfileMapper;
import com.service.api.idmhperu.dto.request.ProfileMenuRequest;
import com.service.api.idmhperu.dto.request.ProfileRequest;
import com.service.api.idmhperu.dto.request.ProfileStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.MenuResponse;
import com.service.api.idmhperu.dto.response.ProfileResponse;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.MenuRepository;
import com.service.api.idmhperu.repository.ProfileRepository;
import com.service.api.idmhperu.repository.spec.ProfileSpecification;
import com.service.api.idmhperu.service.ProfileService;
import com.service.api.idmhperu.util.JwtUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

  private final ProfileRepository repository;
  private final MenuRepository menuRepository;
  private final ProfileMapper mapper;
  private final MenuMapper menuMapper;

  @Override
  public ApiResponse<List<ProfileResponse>> findAll(ProfileFilter filter) {
    return new ApiResponse<>(
        "Perfiles listados correctamente",
        mapper.toResponseList(
            repository.findAll(ProfileSpecification.byFilter(filter))
        )
    );
  }

  @Override
  public ApiResponse<ProfileResponse> findById(Long id) {
    Profile profile = findOrThrow(id);
    return new ApiResponse<>("Perfil encontrado", mapper.toResponse(profile));
  }

  @Override
  public ApiResponse<ProfileResponse> create(ProfileRequest request) {
    Profile profile = new Profile();
    profile.setName(request.getName());
    profile.setStatus(request.getStatus());
    profile.setCreatedBy(JwtUtils.extractUsernameFromContext());
    return new ApiResponse<>(
        "Perfil registrado correctamente",
        mapper.toResponse(repository.save(profile))
    );
  }

  @Override
  public ApiResponse<ProfileResponse> update(Long id, ProfileRequest request) {
    Profile profile = findOrThrow(id);
    profile.setName(request.getName());
    profile.setStatus(request.getStatus());
    profile.setUpdatedBy(JwtUtils.extractUsernameFromContext());
    return new ApiResponse<>(
        "Perfil actualizado correctamente",
        mapper.toResponse(repository.save(profile))
    );
  }

  @Override
  public ApiResponse<Void> updateStatus(Long id, ProfileStatusRequest request) {
    Profile profile = findOrThrow(id);
    profile.setStatus(request.getStatus());
    profile.setUpdatedBy(JwtUtils.extractUsernameFromContext());
    repository.save(profile);
    return new ApiResponse<>("Estado actualizado correctamente", null);
  }

  @Override
  @Transactional
  public ApiResponse<List<MenuResponse>> getMenus(Long id) {
    Profile profile = findOrThrow(id);
    List<Menu> menus = new ArrayList<>(profile.getMenus());
    menus.sort((a, b) -> {
      int cmp = a.getSortOrder().compareTo(b.getSortOrder());
      return cmp != 0 ? cmp : a.getId().compareTo(b.getId());
    });
    return new ApiResponse<>("Permisos listados correctamente", menuMapper.toResponseList(menus));
  }

  @Override
  @Transactional
  public ApiResponse<Void> updateMenus(Long id, ProfileMenuRequest request) {
    Profile profile = findOrThrow(id);
    List<Menu> menus = menuRepository.findAllById(request.getMenuIds());
    profile.setMenus(new HashSet<>(menus));
    profile.setUpdatedBy(JwtUtils.extractUsernameFromContext());
    repository.save(profile);
    return new ApiResponse<>("Permisos actualizados correctamente", null);
  }

  private Profile findOrThrow(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado"));
  }
}
