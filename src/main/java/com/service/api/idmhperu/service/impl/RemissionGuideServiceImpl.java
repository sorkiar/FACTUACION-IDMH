package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.Carrier;
import com.service.api.idmhperu.dto.entity.Client;
import com.service.api.idmhperu.dto.entity.ClientAddress;
import com.service.api.idmhperu.dto.entity.DocumentSeries;
import com.service.api.idmhperu.dto.entity.Driver;
import com.service.api.idmhperu.dto.entity.DriverVehicle;
import com.service.api.idmhperu.dto.entity.RemissionGuide;
import com.service.api.idmhperu.dto.entity.RemissionGuideDriver;
import com.service.api.idmhperu.dto.entity.RemissionGuideItem;
import com.service.api.idmhperu.dto.entity.TransferReason;
import com.service.api.idmhperu.dto.filter.RemissionGuideFilter;
import com.service.api.idmhperu.dto.mapper.RemissionGuideMapper;
import com.service.api.idmhperu.dto.request.RemissionGuideDriverRequest;
import com.service.api.idmhperu.dto.request.RemissionGuideItemRequest;
import com.service.api.idmhperu.dto.request.RemissionGuideRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.RemissionGuideResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.job.SunatDocumentJobService;
import com.service.api.idmhperu.repository.CarrierRepository;
import com.service.api.idmhperu.repository.ClientAddressRepository;
import com.service.api.idmhperu.repository.ClientRepository;
import com.service.api.idmhperu.repository.DocumentSeriesRepository;
import com.service.api.idmhperu.repository.DriverRepository;
import com.service.api.idmhperu.repository.DriverVehicleRepository;
import com.service.api.idmhperu.repository.ProductRepository;
import com.service.api.idmhperu.repository.RemissionGuideDriverRepository;
import com.service.api.idmhperu.repository.RemissionGuideItemRepository;
import com.service.api.idmhperu.repository.RemissionGuideRepository;
import com.service.api.idmhperu.repository.TransferReasonRepository;
import com.service.api.idmhperu.repository.spec.RemissionGuideSpecification;
import com.service.api.idmhperu.service.RemissionGuidePdfService;
import com.service.api.idmhperu.service.RemissionGuideService;
import com.service.api.idmhperu.util.JwtUtils;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemissionGuideServiceImpl implements RemissionGuideService {

  private final RemissionGuideRepository guideRepository;
  private final RemissionGuideItemRepository itemRepository;
  private final RemissionGuideDriverRepository driverRepository;
  private final DocumentSeriesRepository documentSeriesRepository;
  private final ProductRepository productRepository;
  private final ClientRepository clientRepository;
  private final ClientAddressRepository clientAddressRepository;
  private final CarrierRepository carrierRepository;
  private final DriverRepository driverMasterRepository;
  private final DriverVehicleRepository vehicleRepository;
  private final TransferReasonRepository transferReasonRepository;
  private final com.service.api.idmhperu.service.ConfigurationService configurationService;
  private final RemissionGuideMapper mapper;
  private final RemissionGuidePdfService pdfService;
  private final SunatDocumentJobService sunatDocumentJobService;
  private final com.service.api.idmhperu.service.SunatSendConfigService sunatSendConfigService;

  @Override
  public ApiResponse<List<RemissionGuideResponse>> findAll(RemissionGuideFilter filter) {
    List<RemissionGuide> guides = guideRepository.findAll(
        RemissionGuideSpecification.byFilter(filter));
    return new ApiResponse<>("Guías listadas correctamente", mapper.toResponseList(guides));
  }

  @Override
  public ApiResponse<RemissionGuideResponse> findById(Long id) {
    RemissionGuide guide = guideRepository.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException("Guía de remisión no encontrada"));
    return new ApiResponse<>("Guía obtenida correctamente", mapper.toResponse(guide));
  }

  @Override
  @Transactional
  public ApiResponse<RemissionGuideResponse> create(RemissionGuideRequest request) {

    String username = JwtUtils.extractUsernameFromContext();

    // 1. Validaciones de negocio
    if ("TRANSPORTE_PRIVADO".equals(request.getTransportMode())) {
      if (request.getDrivers() == null || request.getDrivers().isEmpty()) {
        throw new BusinessValidationException(
            "Debe registrar al menos un conductor para TRANSPORTE_PRIVADO");
      }
    }

    if ("TRANSPORTE_PUBLICO".equals(request.getTransportMode())) {
      if (request.getCarrierId() == null) {
        throw new BusinessValidationException(
            "Se requiere carrierId para TRANSPORTE_PUBLICO");
      }
    }

    TransferReason transferReason = transferReasonRepository.findById(request.getTransferReasonId())
        .orElseThrow(() -> new ResourceNotFoundException("Motivo de traslado no encontrado"));
    if (transferReason.getStatus() != 1) {
      throw new BusinessValidationException("El motivo de traslado seleccionado no está activo");
    }
    if ("13".equals(transferReason.getCode())) {
      if (request.getTransferReasonDescription() == null
          || request.getTransferReasonDescription().isBlank()) {
        throw new BusinessValidationException(
            "transferReasonDescription es obligatorio cuando el motivo es Otros (13)");
      }
    }

    if ("04".equals(transferReason.getCode())) {
      if (request.getOriginLocalCode() == null || request.getOriginLocalCode().isBlank()) {
        throw new BusinessValidationException(
            "originLocalCode es obligatorio para motivo 04 - Traslado entre establecimientos");
      }
      if (request.getDestinationLocalCode() == null || request.getDestinationLocalCode().isBlank()) {
        throw new BusinessValidationException(
            "destinationLocalCode es obligatorio para motivo 04 - Traslado entre establecimientos");
      }
    }

    // 2. Reservar secuencia en la serie (con lock pesimista)
    DocumentSeries series = documentSeriesRepository
        .findActiveByDocumentTypeCodeForUpdate("09", 1)
        .stream().findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(
            "No se encontró una serie activa para Guía de Remisión (09)"));

    Integer nextSequence = series.getCurrentSequence() + 1;
    series.setCurrentSequence(nextSequence);
    documentSeriesRepository.save(series);

    // 3. Crear la guía
    RemissionGuide guide = new RemissionGuide();
    guide.setDocumentSeries(series);
    guide.setSeries(series.getSeries());
    guide.setSequence(String.format("%08d", nextSequence));
    guide.setIssueDate(LocalDateTime.now());
    guide.setTransferDate(request.getTransferDate());
    guide.setTransferReason(transferReason);
    guide.setTransferReasonDescription(request.getTransferReasonDescription());
    guide.setTransportMode(request.getTransportMode());
    guide.setGrossWeight(request.getGrossWeight());
    guide.setWeightUnit(request.getWeightUnit() != null ? request.getWeightUnit() : "KGM");
    guide.setPackageCount(request.getPackageCount() != null ? request.getPackageCount() : 1);
    guide.setOriginAddress(request.getOriginAddress());
    guide.setOriginUbigeo(request.getOriginUbigeo());
    guide.setOriginLocalCode("04".equals(transferReason.getCode()) ? request.getOriginLocalCode() : null);
    guide.setDestinationAddress(request.getDestinationAddress());
    guide.setDestinationUbigeo(request.getDestinationUbigeo());
    guide.setDestinationLocalCode("04".equals(transferReason.getCode()) ? request.getDestinationLocalCode() : null);
    guide.setMinorVehicleTransfer(
        request.getMinorVehicleTransfer() != null && request.getMinorVehicleTransfer());

    // Destinatario (cliente)
    Client client = clientRepository.findById(request.getClientId())
        .orElseThrow(() -> new ResourceNotFoundException("Destinatario no encontrado"));

    if ("04".equals(transferReason.getCode())) {
      String companyRuc = configurationService.getGroup("empresa_emisora").get("emprRuc");
      if (!companyRuc.equals(client.getDocumentNumber())) {
        throw new BusinessValidationException(
            "Para motivo 04, el cliente destino debe ser el propio emisor (RUC: " + companyRuc + ")");
      }
    }

    guide.setClient(client);

    // Dirección del cliente para el comprobante (texto obligatorio del request)
    guide.setClientAddress(request.getClientAddress());

    // Dirección registrada del cliente (referencial, nullable)
    if (request.getClientAddressId() != null) {
      ClientAddress selectedAddress = clientAddressRepository
          .findByIdAndClientIdAndDeletedAtIsNull(request.getClientAddressId(), client.getId())
          .orElseThrow(
              () -> new ResourceNotFoundException("Dirección del destinatario no encontrada"));
      guide.setSelectedClientAddress(selectedAddress);
    }

    // Transportista (TRANSPORTE_PUBLICO → master carrier)
    if (request.getCarrierId() != null) {
      Carrier carrier = carrierRepository.findById(request.getCarrierId())
          .filter(c -> Integer.valueOf(1).equals(c.getStatus()))
          .orElseThrow(() -> new ResourceNotFoundException("Transportista no encontrado o inactivo"));
      guide.setCarrier(carrier);
    }

    guide.setObservations(request.getObservations());
    guide.setStatus("PENDIENTE");
    guide.setCreatedBy(username);

    guide = guideRepository.save(guide);

    // 4. Guardar ítems
    for (RemissionGuideItemRequest itemReq : request.getItems()) {
      RemissionGuideItem item = new RemissionGuideItem();
      item.setRemissionGuide(guide);
      item.setDescription(itemReq.getDescription());
      item.setQuantity(itemReq.getQuantity());
      item.setUnitMeasureSunat(
          itemReq.getUnitMeasureSunat() != null ? itemReq.getUnitMeasureSunat() : "NIU");

      BigDecimal unitPrice = itemReq.getUnitPrice();
      BigDecimal valorUnitario = unitPrice.divide(new BigDecimal("1.18"), 6, RoundingMode.HALF_UP);
      BigDecimal subtotal = valorUnitario.multiply(itemReq.getQuantity())
          .setScale(2, RoundingMode.HALF_UP);
      BigDecimal tax = subtotal.multiply(new BigDecimal("0.18"))
          .setScale(2, RoundingMode.HALF_UP);
      BigDecimal total = subtotal.add(tax);

      item.setUnitPrice(unitPrice);
      item.setSubtotalAmount(subtotal);
      item.setTaxAmount(tax);
      item.setTotalAmount(total);
      item.setCreatedBy(username);

      if (itemReq.getProductId() != null) {
        item.setProduct(productRepository.findById(itemReq.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado")));
      }

      itemRepository.save(item);
    }

    // 5. Guardar conductores (TRANSPORTE_PRIVADO → master driver + specific vehicle)
    if (request.getDrivers() != null) {
      for (RemissionGuideDriverRequest driverReq : request.getDrivers()) {
        Driver driver = driverMasterRepository.findById(driverReq.getDriverId())
            .filter(d -> Integer.valueOf(1).equals(d.getStatus()))
            .orElseThrow(() -> new ResourceNotFoundException(
                "Conductor no encontrado o inactivo: id=" + driverReq.getDriverId()));

        RemissionGuideDriver guideDriver = new RemissionGuideDriver();
        guideDriver.setRemissionGuide(guide);
        guideDriver.setDriver(driver);

        if (driverReq.getVehiclePlateId() != null) {
          DriverVehicle vehicle = vehicleRepository
              .findByIdAndDriverIdAndDeletedAtIsNull(driverReq.getVehiclePlateId(), driver.getId())
              .orElseThrow(() -> new ResourceNotFoundException(
                  "Placa no encontrada para el conductor id=" + driverReq.getDriverId()));
          guideDriver.setDriverVehicle(vehicle);
          guideDriver.setVehiclePlate(vehicle.getPlate());
        } else {
          guideDriver.setVehiclePlate(driverReq.getVehiclePlate());
        }

        guideDriver.setCreatedBy(username);
        driverRepository.save(guideDriver);
      }
    }

    // 6. Generar PDF
    pdfService.generatePdf(guide.getId());

    // 7. Enviar a SUNAT solo si está en modo ONLINE
    if (sunatSendConfigService.isOnlineMode("guia_remision")) {
      sunatDocumentJobService.sendRemissionGuideNow(guide);
    }

    RemissionGuide saved = guideRepository.findByIdAndDeletedAtIsNull(guide.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Guía de remisión no encontrada"));

    return new ApiResponse<>("Guía de remisión registrada correctamente", mapper.toResponse(saved));
  }
}
