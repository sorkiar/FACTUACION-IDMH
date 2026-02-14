package com.service.api.idmhperu.service.impl;

import com.service.api.idmhperu.dto.entity.ChargeUnit;
import com.service.api.idmhperu.dto.entity.ServiceCategory;
import com.service.api.idmhperu.dto.filter.ServiceFilter;
import com.service.api.idmhperu.dto.mapper.ServiceMapper;
import com.service.api.idmhperu.dto.request.ServiceRequest;
import com.service.api.idmhperu.dto.request.ServiceStatusRequest;
import com.service.api.idmhperu.dto.response.ApiResponse;
import com.service.api.idmhperu.dto.response.ServiceResponse;
import com.service.api.idmhperu.exception.BusinessValidationException;
import com.service.api.idmhperu.exception.ResourceNotFoundException;
import com.service.api.idmhperu.repository.ChargeUnitRepository;
import com.service.api.idmhperu.repository.ServiceCategoryRepository;
import com.service.api.idmhperu.repository.ServiceRepository;
import com.service.api.idmhperu.repository.spec.ServiceSpecification;
import com.service.api.idmhperu.service.GoogleDriveService;
import com.service.api.idmhperu.service.ServiceService;
import com.service.api.idmhperu.service.SkuSequenceService;
import com.service.api.idmhperu.util.JwtUtils;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
  private final ServiceRepository repository;
  private final ServiceCategoryRepository serviceCategoryRepository;
  private final ChargeUnitRepository chargeUnitRepository;
  private final ServiceMapper mapper;
  private final GoogleDriveService googleDriveService;
  private final SkuSequenceService skuSequenceService;

  // IDs reales de carpetas en Drive
  private static final String SERVICE_IMAGE_FOLDER_ID = "1-tBwykk9Wcm-43CtV0M_q8gF7aBs2VQ_";
  private static final String SERVICE_TECH_SHEET_FOLDER_ID = "1GFwVCReV9K4wKwJQ9T95egcO0wqNFpaW";

  @Override
  public ApiResponse<List<ServiceResponse>> findAll(ServiceFilter filter) {
    return new ApiResponse<>(
        "Servicios listados correctamente",
        mapper.toResponseList(
            repository.findAll(ServiceSpecification.byFilter(filter))
        )
    );
  }

  @Override
  @Transactional
  public ApiResponse<ServiceResponse> create(
      ServiceRequest request,
      MultipartFile image,
      MultipartFile technicalSheet
  ) {
    ServiceCategory category = serviceCategoryRepository.findById(
        request.getServiceCategoryId()
    ).orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

    ChargeUnit chargeUnit = chargeUnitRepository.findById(
        request.getChargeUnitId()
    ).orElseThrow(() -> new ResourceNotFoundException("Unidad de cobro no encontrada"));

    String sku = skuSequenceService.registerSku("SRV");

    if (repository.existsBySku(sku)) {
      throw new BusinessValidationException("El SKU ya existe");
    }

    try {
      String imageUrl = null;
      if (image != null && !image.isEmpty()) {
        File imageFile = convertMultipartToFile(
            image,
            sku + "_image"
        );

        imageUrl = googleDriveService.uploadFileWithPublicAccess(
            imageFile,
            SERVICE_IMAGE_FOLDER_ID
        );
      }

      String technicalSheetUrl = null;
      if (technicalSheet != null && !technicalSheet.isEmpty()) {
        File pdfFile = convertMultipartToFile(
            technicalSheet,
            sku + "_tech_sheet"
        );

        String fileId = googleDriveService.uploadPdf(
            pdfFile,
            SERVICE_TECH_SHEET_FOLDER_ID
        );

        technicalSheetUrl =
            "https://drive.google.com/file/d/" + fileId + "/view";
      }

      com.service.api.idmhperu.dto.entity.Service service =
          new com.service.api.idmhperu.dto.entity.Service();

      service.setSku(sku);
      service.setName(request.getName());
      service.setServiceCategory(category);
      service.setChargeUnit(chargeUnit);
      service.setPrice(request.getPrice());
      service.setEstimatedTime(request.getEstimatedTime());
      service.setExpectedDelivery(request.getExpectedDelivery());
      service.setIncludesDescription(request.getIncludesDescription());
      service.setExcludesDescription(request.getExcludesDescription());
      service.setConditions(request.getConditions());
      service.setRequiresMaterials(request.getRequiresMaterials());
      service.setRequiresSpecification(request.getRequiresSpecification());
      service.setShortDescription(request.getShortDescription());
      service.setDetailedDescription(request.getDetailedDescription());
      service.setImageUrl(imageUrl);
      service.setTechnicalSheetUrl(technicalSheetUrl);
      service.setStatus(1);
      service.setCreatedBy(JwtUtils.extractUsernameFromContext());

      return new ApiResponse<>(
          "Servicio registrado correctamente",
          mapper.toResponse(repository.save(service))
      );

    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessValidationException(
          "Error al subir archivos a Google Drive: " + e.getMessage()
      );
    }
  }

  @Override
  @Transactional
  public ApiResponse<ServiceResponse> update(
      Long id,
      ServiceRequest request,
      MultipartFile image,
      MultipartFile technicalSheet
  ) {

    com.service.api.idmhperu.dto.entity.Service service =
        repository.findByIdAndStatusNot(id, 2)
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

    try {
      if (image != null && !image.isEmpty()) {
        File imageFile = convertMultipartToFile(
            image,
            service.getSku() + "_image"
        );

        String imageUrl = googleDriveService.uploadFileWithPublicAccess(
            imageFile,
            SERVICE_IMAGE_FOLDER_ID
        );

        service.setImageUrl(imageUrl);
        imageFile.delete();
      }

      if (technicalSheet != null && !technicalSheet.isEmpty()) {
        File pdfFile = convertMultipartToFile(
            technicalSheet,
            service.getSku() + "_tech_sheet"
        );

        String fileId = googleDriveService.uploadPdf(
            pdfFile,
            SERVICE_TECH_SHEET_FOLDER_ID
        );

        String technicalSheetUrl =
            "https://drive.google.com/file/d/" + fileId + "/view";

        service.setTechnicalSheetUrl(technicalSheetUrl);
        pdfFile.delete();
      }

      service.setName(request.getName());
      service.setPrice(request.getPrice());
      service.setEstimatedTime(request.getEstimatedTime());
      service.setExpectedDelivery(request.getExpectedDelivery());
      service.setIncludesDescription(request.getIncludesDescription());
      service.setExcludesDescription(request.getExcludesDescription());
      service.setConditions(request.getConditions());
      service.setRequiresMaterials(request.getRequiresMaterials());
      service.setRequiresSpecification(request.getRequiresSpecification());
      service.setShortDescription(request.getShortDescription());
      service.setDetailedDescription(request.getDetailedDescription());
      service.setUpdatedBy(JwtUtils.extractUsernameFromContext());

      return new ApiResponse<>(
          "Servicio actualizado correctamente",
          mapper.toResponse(repository.save(service))
      );

    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessValidationException(
          "Error al actualizar archivos del servicio: " + e.getMessage()
      );
    }
  }

  @Override
  @Transactional
  public ApiResponse<Void> updateStatus(Long id, ServiceStatusRequest request) {
    com.service.api.idmhperu.dto.entity.Service service = repository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

    service.setStatus(request.getStatus());
    service.setUpdatedBy(JwtUtils.extractUsernameFromContext());

    repository.save(service);

    return new ApiResponse<>("Estado actualizado correctamente", null);
  }

  private File convertMultipartToFile(
      MultipartFile multipart,
      String prefix
  ) throws IOException {

    File file = File.createTempFile(prefix, "");
    // File file = File.createTempFile(prefix, "_" + multipart.getOriginalFilename());
    multipart.transferTo(file);
    return file;
  }

}
