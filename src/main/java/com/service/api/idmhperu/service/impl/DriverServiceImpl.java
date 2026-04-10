package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Driver;
import com.service.api.idmhperu.dto.entity.DriverVehicle;
import com.service.api.idmhperu.dto.filter.DriverFilter;
import com.service.api.idmhperu.dto.mapper.DriverMapper;
import com.service.api.idmhperu.dto.request.DriverRequest;
import com.service.api.idmhperu.dto.request.DriverStatusRequest;
import com.service.api.idmhperu.dto.request.DriverVehicleRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.DriverResponse;
import com.service.api.idmhperu.dto.response.DriverVehicleResponse;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.DriverRepository;
import com.service.api.idmhperu.repository.DriverVehicleRepository;
import com.service.api.idmhperu.repository.spec.DriverSpecification;
import com.service.api.idmhperu.service.DriverService;
import com.service.api.idmhperu.util.JwtUtils;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

  private final DriverRepository repository;
  private final DriverVehicleRepository vehicleRepository;
  private final DriverMapper mapper;

  @Override
  public ApiResponse<List<DriverResponse>> findAll(DriverFilter filter) {
    List<Driver> drivers = repository.findAll(DriverSpecification.byFilter(filter));
    return new ApiResponse<>("Conductores listados correctamente", mapper.toResponseList(drivers));
  }

  @Override
  public ApiResponse<DriverResponse> findById(Long id) {
    Driver driver = repository.findById(id)
        .filter(d -> !Integer.valueOf(2).equals(d.getStatus()))
        .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado"));
    return new ApiResponse<>("Conductor obtenido correctamente",
        mapper.toResponseWithVehicles(driver));
  }

  @Override
  public ApiResponse<DriverResponse> create(DriverRequest request) {
    String username = JwtUtils.extractUsernameFromContext();

    Driver driver = new Driver();
    driver.setDocType(request.getDocType() != null ? request.getDocType() : "DNI");
    driver.setDocNumber(request.getDocNumber());
    driver.setFirstName(request.getFirstName());
    driver.setLastName(request.getLastName());
    driver.setLicenseNumber(request.getLicenseNumber());
    driver.setStatus(1);
    driver.setCreatedBy(username);

    return new ApiResponse<>("Conductor registrado correctamente",
        mapper.toResponseWithVehicles(repository.save(driver)));
  }

  @Override
  public ApiResponse<DriverResponse> update(Long id, DriverRequest request) {
    String username = JwtUtils.extractUsernameFromContext();

    Driver driver = repository.findById(id)
        .filter(d -> !Integer.valueOf(2).equals(d.getStatus()))
        .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado"));

    driver.setDocType(request.getDocType() != null ? request.getDocType() : "DNI");
    driver.setDocNumber(request.getDocNumber());
    driver.setFirstName(request.getFirstName());
    driver.setLastName(request.getLastName());
    driver.setLicenseNumber(request.getLicenseNumber());
    driver.setUpdatedBy(username);

    return new ApiResponse<>("Conductor actualizado correctamente",
        mapper.toResponseWithVehicles(repository.save(driver)));
  }

  @Override
  public ApiResponse<Void> updateStatus(Long id, DriverStatusRequest request) {
    String username = JwtUtils.extractUsernameFromContext();

    Driver driver = repository.findById(id)
        .filter(d -> !Integer.valueOf(2).equals(d.getStatus()))
        .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado"));

    driver.setStatus(request.getStatus());
    driver.setUpdatedBy(username);
    repository.save(driver);

    return new ApiResponse<>("Estado actualizado correctamente", null);
  }

  // ── Vehicle plates ───────────────────────────────────────

  @Override
  public ApiResponse<List<DriverVehicleResponse>> findVehicles(Long driverId) {
    requireDriver(driverId);
    List<DriverVehicle> vehicles = vehicleRepository.findByDriverIdAndDeletedAtIsNull(driverId);
    return new ApiResponse<>("Placas listadas correctamente",
        mapper.toVehicleResponseList(vehicles));
  }

  @Override
  public ApiResponse<DriverVehicleResponse> addVehicle(Long driverId, DriverVehicleRequest request) {
    String username = JwtUtils.extractUsernameFromContext();
    Driver driver = requireDriver(driverId);

    DriverVehicle vehicle = new DriverVehicle();
    vehicle.setDriver(driver);
    vehicle.setPlate(request.getPlate().toUpperCase());
    vehicle.setCreatedBy(username);

    return new ApiResponse<>("Placa registrada correctamente",
        mapper.toVehicleResponse(vehicleRepository.save(vehicle)));
  }

  @Override
  public ApiResponse<DriverVehicleResponse> updateVehicle(Long driverId, Long vehicleId,
      DriverVehicleRequest request) {
    String username = JwtUtils.extractUsernameFromContext();

    DriverVehicle vehicle = vehicleRepository
        .findByIdAndDriverIdAndDeletedAtIsNull(vehicleId, driverId)
        .orElseThrow(() -> new ResourceNotFoundException("Placa no encontrada"));

    vehicle.setPlate(request.getPlate().toUpperCase());
    vehicle.setUpdatedBy(username);

    return new ApiResponse<>("Placa actualizada correctamente",
        mapper.toVehicleResponse(vehicleRepository.save(vehicle)));
  }

  @Override
  public ApiResponse<Void> deleteVehicle(Long driverId, Long vehicleId) {
    String username = JwtUtils.extractUsernameFromContext();

    DriverVehicle vehicle = vehicleRepository
        .findByIdAndDriverIdAndDeletedAtIsNull(vehicleId, driverId)
        .orElseThrow(() -> new ResourceNotFoundException("Placa no encontrada"));

    vehicle.setDeletedAt(LocalDateTime.now());
    vehicle.setDeletedBy(username);
    vehicleRepository.save(vehicle);

    return new ApiResponse<>("Placa eliminada correctamente", null);
  }

  private Driver requireDriver(Long driverId) {
    return repository.findById(driverId)
        .filter(d -> !Integer.valueOf(2).equals(d.getStatus()))
        .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado"));
  }
}
