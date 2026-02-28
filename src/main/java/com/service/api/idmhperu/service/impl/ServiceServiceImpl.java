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
import com.service.api.idmhperu.service.ConfigurationService;
import com.service.api.idmhperu.service.GoogleDriveService;
import com.service.api.idmhperu.service.ServiceService;
import com.service.api.idmhperu.service.SkuSequenceService;
import com.service.api.idmhperu.util.JwtUtils;
import jakarta.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
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
  private final ConfigurationService configurationService;

  @Value("${drive.folder-id.servicios-imagenes}")
  private String SERVICE_IMAGE_FOLDER_ID;

  @Value("${drive.folder-id.servicios-fichas}")
  private String SERVICE_TECH_SHEET_FOLDER_ID;

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

  @Override
  public byte[] generatePdf(Long id) {
    com.service.api.idmhperu.dto.entity.Service service =
        repository.findByIdAndStatusNot(id, 2)
            .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

    try {
      Map<String, String> config = configurationService.getGroup("empresa_emisora");

      Map<String, Object> row = new HashMap<>();
      row.put("empr_ruc", config.get("emprRuc"));
      row.put("empr_razon_social", config.get("emprRazonSocial"));
      row.put("empr_nombre_comercial", config.get("emprNombreComercial"));
      row.put("empr_direccion_fiscal", config.get("emprDireccionFiscal"));
      row.put("sku", service.getSku());
      row.put("name", service.getName());
      row.put("category", service.getServiceCategory() != null ? service.getServiceCategory().getName() : "");
      row.put("charge_unit", service.getChargeUnit() != null ? service.getChargeUnit().getName() : "");
      row.put("price", service.getPrice() != null ? service.getPrice().toPlainString() : "-");
      row.put("estimated_time", service.getEstimatedTime());
      row.put("expected_delivery", service.getExpectedDelivery());
      row.put("requires_materials", Boolean.TRUE.equals(service.getRequiresMaterials()) ? "Sí" : "No");
      row.put("requires_specification", Boolean.TRUE.equals(service.getRequiresSpecification()) ? "Sí" : "No");
      row.put("short_description", service.getShortDescription());
      row.put("detailed_description", service.getDetailedDescription());
      row.put("includes_description", service.getIncludesDescription());
      row.put("excludes_description", service.getExcludesDescription());
      row.put("conditions", service.getConditions());
      row.put("image_url", service.getImageUrl());
      row.put("technical_sheet_url", service.getTechnicalSheetUrl());

      JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of(row));

      InputStream inputStream = getClass().getResourceAsStream("/jasper/ServicioFichaA4.jrxml");
      JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

      Map<String, Object> parameters = new HashMap<>();
      parameters.put("urlImagen",
          Objects.requireNonNull(getClass().getResource("/img/logo.png")).toString());

      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      JasperExportManager.exportReportToPdfStream(jasperPrint, out);
      return out.toByteArray();

    } catch (Exception e) {
      e.printStackTrace();
      throw new BusinessValidationException("Error al generar PDF del servicio: " + e.getMessage());
    }
  }

  private File convertMultipartToFile(
      MultipartFile multipart,
      String prefix
  ) throws IOException {

    File file = File.createTempFile(prefix, "");
    multipart.transferTo(file);
    return file;
  }

}
