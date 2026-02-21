package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.DocumentSeries;
import com.service.api.idmhperu.dto.entity.RemissionGuide;
import com.service.api.idmhperu.dto.entity.RemissionGuideDriver;
import com.service.api.idmhperu.dto.entity.RemissionGuideItem;
import com.service.api.idmhperu.dto.filter.RemissionGuideFilter;
import com.service.api.idmhperu.dto.mapper.RemissionGuideMapper;
import com.service.api.idmhperu.dto.request.RemissionGuideDriverRequest;
import com.service.api.idmhperu.dto.request.RemissionGuideItemRequest;
import com.service.api.idmhperu.dto.request.RemissionGuideRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.RemissionGuideResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.DocumentSeriesRepository;
import com.service.api.idmhperu.repository.ProductRepository;
import com.service.api.idmhperu.repository.RemissionGuideDriverRepository;
import com.service.api.idmhperu.repository.RemissionGuideItemRepository;
import com.service.api.idmhperu.repository.RemissionGuideRepository;
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
  private final RemissionGuideMapper mapper;
  private final RemissionGuidePdfService pdfService;

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
      if (request.getCarrierDocNumber() == null || request.getCarrierDocNumber().isBlank()) {
        throw new BusinessValidationException(
            "Se requieren datos del transportista para TRANSPORTE_PUBLICO");
      }
    }

    if ("OTROS".equals(request.getTransferReason())) {
      if (request.getTransferReasonDescription() == null
          || request.getTransferReasonDescription().isBlank()) {
        throw new BusinessValidationException(
            "transferReasonDescription es obligatorio cuando transferReason = OTROS");
      }
    }

    // 2. Reservar secuencia en la serie (con lock pesimista)
    DocumentSeries series = documentSeriesRepository
        .findByIdForUpdate(7L)
        .orElseThrow(() -> new ResourceNotFoundException("Serie no encontrada"));

    if (!"09".equals(series.getDocumentTypeSunat().getCode())) {
      throw new BusinessValidationException(
          "La serie seleccionada no corresponde a Guía de Remisión (09)");
    }

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
    guide.setTransferReason(request.getTransferReason());
    guide.setTransferReasonDescription(request.getTransferReasonDescription());
    guide.setTransportMode(request.getTransportMode());
    guide.setGrossWeight(request.getGrossWeight());
    guide.setWeightUnit(request.getWeightUnit() != null ? request.getWeightUnit() : "KGM");
    guide.setPackageCount(request.getPackageCount() != null ? request.getPackageCount() : 1);
    guide.setOriginAddress(request.getOriginAddress());
    guide.setOriginUbigeo(request.getOriginUbigeo());
    guide.setOriginLocalCode(request.getOriginLocalCode());
    guide.setDestinationAddress(request.getDestinationAddress());
    guide.setDestinationUbigeo(request.getDestinationUbigeo());
    guide.setDestinationLocalCode(request.getDestinationLocalCode());
    guide.setMinorVehicleTransfer(
        request.getMinorVehicleTransfer() != null && request.getMinorVehicleTransfer());
    guide.setRecipientDocType(request.getRecipientDocType());
    guide.setRecipientDocNumber(request.getRecipientDocNumber());
    guide.setRecipientName(request.getRecipientName());
    guide.setRecipientAddress(request.getRecipientAddress());
    guide.setCarrierDocType(request.getCarrierDocType());
    guide.setCarrierDocNumber(request.getCarrierDocNumber());
    guide.setCarrierName(request.getCarrierName());
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

    // 5. Guardar conductores (TRANSPORTE_PRIVADO)
    if (request.getDrivers() != null) {
      for (RemissionGuideDriverRequest driverReq : request.getDrivers()) {
        RemissionGuideDriver driver = new RemissionGuideDriver();
        driver.setRemissionGuide(guide);
        driver.setDriverDocType(driverReq.getDocType());
        driver.setDriverDocNumber(driverReq.getDocNumber());
        driver.setDriverFirstName(driverReq.getFirstName());
        driver.setDriverLastName(driverReq.getLastName());
        driver.setDriverLicenseNumber(driverReq.getLicenseNumber());
        driver.setVehiclePlate(driverReq.getVehiclePlate());
        driver.setCreatedBy(username);
        driverRepository.save(driver);
      }
    }

    // 6. Generar PDF
    pdfService.generatePdf(guide.getId());

    RemissionGuide saved = guideRepository.findByIdAndDeletedAtIsNull(guide.getId())
        .orElseThrow(() -> new ResourceNotFoundException("Guía de remisión no encontrada"));

    return new ApiResponse<>("Guía de remisión registrada correctamente", mapper.toResponse(saved));
  }
}
