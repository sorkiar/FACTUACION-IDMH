package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Menu;
import com.service.api.idmhperu.dto.mapper.MenuMapper;
import com.service.api.idmhperu.dto.request.MenuRequest;
import com.service.api.idmhperu.dto.request.MenuStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.MenuResponse;
import com.service.api.idmhperu.dto.response.SidebarItemResponse;
import com.service.api.idmhperu.dto.response.SidebarSubItemResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.MenuRepository;
import com.service.api.idmhperu.repository.UserRepository;
import com.service.api.idmhperu.service.MenuService;
import com.service.api.idmhperu.util.JwtUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

  private final MenuRepository repository;
  private final UserRepository userRepository;
  private final MenuMapper mapper;

  @Override
  public ApiResponse<List<MenuResponse>> findAll() {
    return new ApiResponse<>(
        "Menús listados correctamente",
        mapper.toResponseList(repository.findAllByMenuTypeWithParent("SIDEBAR"))
    );
  }

  @Override
  public ApiResponse<MenuResponse> findById(Long id) {
    return new ApiResponse<>("Menú encontrado", mapper.toResponse(findOrThrow(id)));
  }

  @Override
  public ApiResponse<MenuResponse> create(MenuRequest request) {
    if (repository.existsByName(request.getName())) {
      throw new BusinessValidationException("Ya existe un menú con ese nombre");
    }

    Menu menu = new Menu();
    menu.setStatus(1);
    applyRequest(menu, request);
    return new ApiResponse<>("Menú registrado correctamente", mapper.toResponse(repository.save(menu)));
  }

  @Override
  public ApiResponse<MenuResponse> update(Long id, MenuRequest request) {
    Menu menu = findOrThrow(id);

    if (repository.existsByNameAndIdNot(request.getName(), id)) {
      throw new BusinessValidationException("Ya existe un menú con ese nombre");
    }

    applyRequest(menu, request);
    return new ApiResponse<>("Menú actualizado correctamente", mapper.toResponse(repository.save(menu)));
  }

  @Override
  public ApiResponse<Void> updateStatus(Long id, MenuStatusRequest request) {
    Menu menu = findOrThrow(id);
    menu.setStatus(request.getStatus());
    repository.save(menu);
    return new ApiResponse<>("Estado actualizado correctamente", null);
  }

  @Override
  @Transactional(readOnly = true)
  public ApiResponse<List<SidebarItemResponse>> getSidebar() {
    return buildMenuResponse("SIDEBAR", "Sidebar cargado correctamente");
  }

  @Override
  @Transactional(readOnly = true)
  public ApiResponse<List<SidebarItemResponse>> getNavbar() {
    return buildMenuResponse("NAVBAR", "Navbar cargado correctamente");
  }

  @Override
  @Transactional(readOnly = true)
  public ApiResponse<List<SidebarItemResponse>> getInternal() {
    return buildMenuResponse("INTERNAL", "Menús internos cargados correctamente");
  }

  private ApiResponse<List<SidebarItemResponse>> buildMenuResponse(String menuType, String message) {
    String username = JwtUtils.extractUsernameFromContext();

    com.service.api.idmhperu.dto.entity.User user = userRepository.findWithMenusByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

    List<Menu> allowed = user.getProfile().getMenus().stream()
        .filter(m -> m.getStatus() == 1 && menuType.equals(m.getMenuType()))
        .sorted(Comparator.comparingInt(Menu::getSortOrder).thenComparingLong(Menu::getId))
        .collect(Collectors.toList());

    Map<Long, List<Menu>> byParent = allowed.stream()
        .filter(m -> m.getParent() != null)
        .collect(Collectors.groupingBy(m -> m.getParent().getId()));

    List<SidebarItemResponse> items = allowed.stream()
        .filter(m -> m.getParent() == null)
        .map(parent -> {
          SidebarItemResponse item = new SidebarItemResponse();
          item.setName(parent.getName());
          item.setPath(parent.getPath());

          List<Menu> children = byParent.get(parent.getId());
          if (children != null) {
            item.setSubItems(children.stream()
                .map(child -> {
                  SidebarSubItemResponse sub = new SidebarSubItemResponse();
                  sub.setName(child.getName());
                  sub.setPath(child.getPath());
                  return sub;
                })
                .collect(Collectors.toList()));
          }
          return item;
        })
        .collect(Collectors.toList());

    return new ApiResponse<>(message, items);
  }

  @Override
  @Transactional(readOnly = true)
  public ApiResponse<List<String>> getAllowedPaths() {
    String username = JwtUtils.extractUsernameFromContext();

    com.service.api.idmhperu.dto.entity.User user = userRepository.findWithMenusByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

    List<String> paths = user.getProfile().getMenus().stream()
        .filter(m -> m.getStatus() == 1 && m.getPath() != null)
        .map(Menu::getPath)
        .collect(Collectors.toList());

    return new ApiResponse<>("Rutas permitidas cargadas correctamente", paths);
  }

  private void applyRequest(Menu menu, MenuRequest request) {
    menu.setName(request.getName());
    menu.setPath(request.getPath());
    menu.setSortOrder(request.getSortOrder());
    menu.setParent(request.getParentId() != null ? findOrThrow(request.getParentId()) : null);
    menu.setMenuType("SIDEBAR");
  }

  private Menu findOrThrow(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Menú no encontrado"));
  }
}
